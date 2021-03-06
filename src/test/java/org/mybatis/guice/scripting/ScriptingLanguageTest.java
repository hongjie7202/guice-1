/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.guice.scripting;

import static com.google.inject.name.Names.bindProperties;
import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;
import org.mybatis.guice.datasource.helper.JdbcHelper;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ScriptingLanguageTest {
  private SqlSessionFactory factory;

  @Before
  public void beforeClass() {
    Injector injector = Guice.createInjector(JdbcHelper.HSQLDB_IN_MEMORY_NAMED, new MyBatisModule() {
      @Override
      protected void initialize() {
        final Properties myBatisProperties = new Properties();
        myBatisProperties.setProperty("mybatis.environment.id", "test");
        myBatisProperties.setProperty("JDBC.username", "sa");
        myBatisProperties.setProperty("JDBC.password", "");
        myBatisProperties.setProperty("JDBC.autoCommit", "false");
        bindProperties(binder(), myBatisProperties);
        bindDataSourceProviderType(PooledDataSourceProvider.class);
        bindTransactionFactoryType(JdbcTransactionFactory.class);
        bindDefaultScriptingLanguageType(CustomLanguageDriver.class);
      }
    });
    factory = injector.getInstance(SqlSessionFactory.class);
  }

  @Test
  public void scriptingLanguageAlias() {
    assertEquals(CustomLanguageDriver.class,
        factory.getConfiguration().getDefaultScriptingLanuageInstance().getClass());
    assertEquals(CustomLanguageDriver.class, factory.getConfiguration().getLanguageRegistry().getDefaultDriverClass());
    assertEquals(CustomLanguageDriver.class,
        factory.getConfiguration().getLanguageRegistry().getDriver(CustomLanguageDriver.class).getClass());
  }
}
