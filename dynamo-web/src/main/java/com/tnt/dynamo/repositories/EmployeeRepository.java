package com.tnt.dynamo.repositories;

import com.tnt.dynamo.entities.Employee;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.CollectionUtils;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;


@Singleton
public class EmployeeRepository extends DynamoRepository<Employee> {

    @Override
    protected String tableName() {
        return "Employee";
    }

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeRepository.class);
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_FN = "firstName";
    private static final String ATTRIBUTE_LN = "lastName";

    private static final String ATTRIBUTE_JD = "joiningDate";


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
    public Optional<Employee> findById(@NonNull  Integer id) {
        return findById(Employee.class, id)
                .map(this::employeeOf);
    }


    public void delete(@NonNull  Integer id) {
        delete(Employee.class, id);
    }


    @NonNull
    public List<Employee> findAll() {
        List<Employee> result = new ArrayList<>();
        Integer beforeId = null;
        do {
            QueryRequest request = findAllQueryRequest(Employee.class, beforeId, null);
            QueryResponse response = dynamoDbClient.query(request);
            if (LOG.isTraceEnabled()) {
                LOG.trace(response.toString());
            }
            result.addAll(parseInResponse(response));
            beforeId = lastEvaluatedId(response, Employee.class).orElse(null);
        } while(beforeId != null);
        return result;
    }

    private List<Employee> parseInResponse(QueryResponse response) {
        List<Map<String, AttributeValue>> items = response.items();
        List<Employee> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(items)) {
            for (Map<String, AttributeValue> item : items) {
                result.add(employeeOf(item));
            }
        }
        return result;
    }

    @NonNull
    private Employee employeeOf(@NonNull Map<String, AttributeValue> item) {
        try {
            return new Employee(Integer.parseInt(item.get(ATTRIBUTE_ID).n()),
                    item.get(ATTRIBUTE_FN).s(),
                    item.get(ATTRIBUTE_LN).s(),
                    DateFormat.getDateInstance().parse(item.get(ATTRIBUTE_JD).s()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    @NonNull
    protected Map<String, AttributeValue> item(@NonNull Employee employee) {
        Map<String, AttributeValue> result = super.item(employee);
        result.put(ATTRIBUTE_ID, AttributeValue.builder().s(employee.id().toString()).build());
        result.put(ATTRIBUTE_FN, AttributeValue.builder().s(employee.firstName()).build());
        result.put(ATTRIBUTE_LN, AttributeValue.builder().s(employee.lastName()).build());
        result.put(ATTRIBUTE_JD, AttributeValue.builder().s(employee.joining().toString()).build());
        return result;
    }


}
