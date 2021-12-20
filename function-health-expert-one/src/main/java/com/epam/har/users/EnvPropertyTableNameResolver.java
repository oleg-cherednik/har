package com.epam.har.users;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvPropertyTableNameResolver implements DynamoDBMapperConfig.TableNameResolver {

    public static final EnvPropertyTableNameResolver INSTANCE = new EnvPropertyTableNameResolver();

    private static final String ENV_PROPERTY_PREFIX = "env:";

    private final DynamoDBMapperConfig.TableNameResolver delegate =
            DynamoDBMapperConfig.DefaultTableNameResolver.INSTANCE;

    @Override
    public String getTableName(Class<?> clazz, DynamoDBMapperConfig config) {
        String tableName = delegate.getTableName(clazz, config);
        boolean envProperty = tableName.startsWith(ENV_PROPERTY_PREFIX);

        if (envProperty) {
            String propertyName = tableName.substring(ENV_PROPERTY_PREFIX.length());
            return getMandatoryEnvVariable(propertyName);
        }

        return tableName;
    }

    public static String getMandatoryEnvVariable(String propertyName) {
        String value = System.getenv(propertyName);

        if (value == null) {
            throw new RuntimeException(String.format("Environment variable '%s' not found!", propertyName));
        }

        return value;
    }

}
