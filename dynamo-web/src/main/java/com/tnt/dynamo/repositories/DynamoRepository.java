package com.tnt.dynamo.repositories;

import com.tnt.dynamo.entities.Identified;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.NotNull;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;


public abstract class DynamoRepository<T extends Identified> {

    private static final Logger LOG = LoggerFactory.getLogger(DynamoRepository.class);
    protected final DynamoDbClient dynamoDbClient;

    protected static final String HASH = "#";
    protected static final String ATTRIBUTE_PK = "pk";
    protected static final String ATTRIBUTE_SK = "sk";
    protected static final String ATTRIBUTE_GSI_1_PK = "GSI1PK";
    protected static final String ATTRIBUTE_GSI_1_SK = "GSI1SK";
    protected static final String INDEX_GSI_1 = "GSI1";
    public DynamoRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    protected abstract String tableName();

    public boolean existsTable() {
        try {
            dynamoDbClient.describeTable(DescribeTableRequest.builder()
                    .tableName(tableName())
                    .build());
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public void createTable() {
        dynamoDbClient.createTable(CreateTableRequest.builder()
                .attributeDefinitions(AttributeDefinition.builder()
                                .attributeName(ATTRIBUTE_PK)
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName(ATTRIBUTE_SK)
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName(ATTRIBUTE_GSI_1_PK)
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName(ATTRIBUTE_GSI_1_SK)
                                .attributeType(ScalarAttributeType.S)
                                .build())
                .keySchema(Arrays.asList(KeySchemaElement.builder()
                                .attributeName(ATTRIBUTE_PK)
                                .keyType(KeyType.HASH)
                                .build(),
                        KeySchemaElement.builder()
                                .attributeName(ATTRIBUTE_SK)
                                .keyType(KeyType.RANGE)
                                .build()))
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .tableName(tableName())
                .globalSecondaryIndexes(gsi1())
                .build());
    }

    @NonNull
    public QueryRequest findAllQueryRequest(@NonNull Class<?> cls,
                                            @Nullable Integer beforeId,
                                            @Nullable Integer limit) {
        QueryRequest.Builder builder = QueryRequest.builder()
                .tableName(tableName())
                .indexName(INDEX_GSI_1)
                .scanIndexForward(false);
        if (limit != null) {
            builder.limit(limit);
        }
        if (beforeId == null) {
            return  builder.keyConditionExpression("#pk = :pk")
                    .expressionAttributeNames(Collections.singletonMap("#pk", ATTRIBUTE_GSI_1_PK))
                    .expressionAttributeValues(Collections.singletonMap(":pk",
                            classAttributeValue(cls)))
                    .build();
        } else {
            return builder.keyConditionExpression("#pk = :pk and #sk < :sk")
                    .expressionAttributeNames(CollectionUtils.mapOf("#pk", ATTRIBUTE_GSI_1_PK, "#sk", ATTRIBUTE_GSI_1_SK))
                    .expressionAttributeValues(CollectionUtils.mapOf(":pk",
                            classAttributeValue(cls),
                            ":sk",
                            id(cls, beforeId)
                    ))
                    .build();
        }
    }

    protected void delete(@NonNull @NotNull Class<?> cls, @NonNull Integer id) {
        AttributeValue pk = id(cls, id);
        DeleteItemResponse deleteItemResponse = dynamoDbClient.deleteItem(DeleteItemRequest.builder()
                .tableName(tableName())
                .key(CollectionUtils.mapOf(ATTRIBUTE_PK, pk, ATTRIBUTE_SK, pk))
                .build());
        if (LOG.isDebugEnabled()) {
            LOG.debug(deleteItemResponse.toString());
        }
    }

    protected Optional<Map<String, AttributeValue>> findById(@NonNull @NotNull Class<?> cls, @NonNull Integer id) {
        AttributeValue pk = id(cls, id);
        GetItemResponse getItemResponse = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(tableName())
                .key(CollectionUtils.mapOf(ATTRIBUTE_PK, pk, ATTRIBUTE_SK, pk))
                .build());
        return !getItemResponse.hasItem() ? Optional.empty() : Optional.of(getItemResponse.item());
    }

    @NonNull
    public static Optional<String> lastEvaluatedId(@NonNull QueryResponse response,
                                                   @NonNull Class<?> cls) {
        if (response.hasLastEvaluatedKey()) {
            Map<String, AttributeValue> item = response.lastEvaluatedKey();
            if (item != null && item.containsKey(ATTRIBUTE_PK)) {
                return id(cls, item.get(ATTRIBUTE_PK));
            }
        }
        return Optional.empty();
    }

    private static GlobalSecondaryIndex gsi1() {
        return GlobalSecondaryIndex.builder()
                .indexName(INDEX_GSI_1)
                .keySchema(KeySchemaElement.builder()
                        .attributeName(ATTRIBUTE_GSI_1_PK)
                        .keyType(KeyType.HASH)
                        .build(), KeySchemaElement.builder()
                        .attributeName(ATTRIBUTE_GSI_1_SK)
                        .keyType(KeyType.RANGE)
                        .build())
                .projection(Projection.builder()
                        .projectionType(ProjectionType.ALL)
                        .build())
                .build();
    }

    @NonNull
    protected Map<String, AttributeValue> item(@NonNull T entity) {
        Map<String, AttributeValue> item = new HashMap<>();
        AttributeValue pk = id(entity.getClass(), entity.id());
        item.put(ATTRIBUTE_PK, pk);
        item.put(ATTRIBUTE_SK, pk);
        item.put(ATTRIBUTE_GSI_1_PK, classAttributeValue(entity.getClass()));
        item.put(ATTRIBUTE_GSI_1_SK, pk);
        return item;
    }

    @NonNull
    protected static AttributeValue classAttributeValue(@NonNull Class<?> cls) {
        return AttributeValue.builder()
                .s(cls.getSimpleName())
                .build();
    }

    @NonNull
    protected static AttributeValue id(@NonNull Class<?> cls,
                                       @NonNull Integer id) {
        return AttributeValue.builder()
                .s(String.join(HASH, cls.getSimpleName().toUpperCase(), id.toString()))
                .build();
    }

    @NonNull
    protected static Optional<String> id(@NonNull Class<?> cls,
                                         @NonNull AttributeValue attributeValue) {
        String str = attributeValue.s();
        String substring = cls.getSimpleName().toUpperCase() + HASH;
        return str.startsWith(substring) ? Optional.of(str.substring(substring.length())) : Optional.empty();
    }
}
