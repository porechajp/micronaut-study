package com.tnt.dynamo.repositories;

import com.tnt.dynamo.entities.Employee;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.NotNull;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.awt.print.Book;
import java.util.*;


@Singleton
public class EmployeeRepository extends DynamoRepository<Employee> {

    @Override
    protected String tableName() {
        return "Employee";
    }

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeRepository.class);
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_ISBN = "isbn";
    private static final String ATTRIBUTE_NAME = "name";


    private final IdGenerator idGenerator;

    public EmployeeRepository(DynamoDbClient dynamoDbClient, IdGenerator idGenerator) {
        super(dynamoDbClient);
        this.idGenerator = idGenerator;
    }


    @NonNull
    public Integer save(@NonNull  String firstName,
                       @NonNull  String lastName,
                       @NonNull Date joiningDate) {
        Integer id = idGenerator.generate();
        save(new Employee(id, firstName,lastName,joiningDate));
        return id;
    }

    protected void save(@NonNull Employee employee) {
        PutItemResponse itemResponse = dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(tableName())
                .item(item(employee))
                .build());
        if (LOG.isDebugEnabled()) {
            LOG.debug(itemResponse.toString());
        }
    }


    @NonNull
    public Optional<Book> findById(@NonNull  Integer id) {
        return findById(Employee.class, id)
                .map(this::employeeOf);
    }

    @Override
    public void delete(@NonNull  String id) {
        delete(Book.class, id);
    }

    @Override
    @NonNull
    public List<Book> findAll() {
        List<Book> result = new ArrayList<>();
        String beforeId = null;
        do {
            QueryRequest request = findAllQueryRequest(Book.class, beforeId, null);
            QueryResponse response = dynamoDbClient.query(request);
            if (LOG.isTraceEnabled()) {
                LOG.trace(response.toString());
            }
            result.addAll(parseInResponse(response));
            beforeId = lastEvaluatedId(response, Book.class).orElse(null);
        } while(beforeId != null);
        return result;
    }

    private List<Book> parseInResponse(QueryResponse response) {
        List<Map<String, AttributeValue>> items = response.items();
        List<Book> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(items)) {
            for (Map<String, AttributeValue> item : items) {
                result.add(bookOf(item));
            }
        }
        return result;
    }

    @NonNull
    private Book bookOf(@NonNull Map<String, AttributeValue> item) {
        return new Book(item.get(ATTRIBUTE_ID).s(),
                item.get(ATTRIBUTE_ISBN).s(),
                item.get(ATTRIBUTE_NAME).s());
    }

    @Override
    @NonNull
    protected Map<String, AttributeValue> item(@NonNull Book book) {
        Map<String, AttributeValue> result = super.item(book);
        result.put(ATTRIBUTE_ID, AttributeValue.builder().s(book.getId()).build());
        result.put(ATTRIBUTE_ISBN, AttributeValue.builder().s(book.getIsbn()).build());
        result.put(ATTRIBUTE_NAME, AttributeValue.builder().s(book.getName()).build());
        return result;
    }


}
