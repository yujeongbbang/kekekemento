package com.kekeke.kekekebackend.common.mysql;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.type.StandardBasicTypes;

public class MyFunctionContributor implements org.hibernate.boot.model.FunctionContributor {
public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions.getFunctionRegistry().registerPattern("bitand", "(?1 & ?2)", functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(StandardBasicTypes.INTEGER));
        }
}

//public class MySQLDialectCustom extends MySQLDialect {
//    @Override
//    public void initializeFunctionRegistry(FunctionContributions functionContributions) {
//        super.initializeFunctionRegistry(functionContributions);
//
//        BasicTypeRegistry basicTypeRegistry = functionContributions.getTypeConfiguration().getBasicTypeRegistry();
//
//        functionRegistry.registerPattern(
//                "hstore_find",
//                "(?1 -> ?2 = ?3)",
//                basicTypeRegistry.resolve( StandardBasicTypes.BOOLEAN ));
//        // ...
//    }
//    registerFunction("bitand", new SQLFunctionTemplate(IntegerType.INSTANCE, "(?1 & ?2)"));
//}
