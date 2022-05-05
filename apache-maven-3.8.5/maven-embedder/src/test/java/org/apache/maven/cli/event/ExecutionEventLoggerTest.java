package org.apache.maven.cli.event;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.logging.MessageUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InOrder;
import org.slf4j.Logger;

public class ExecutionEventLoggerTest
{
    private ExecutionEventLogger executionEventLogger;

    @BeforeClass
    public static void setUp() 
    {
        MessageUtils.setColorEnabled( false );
    }

    @AfterClass
    public static void tearDown()
    {
        MessageUtils.setColorEnabled( true );
    }

    @Test
    public void testProjectStarted()
    {
        // prepare
        Logger logger = mock( Logger.class );
        when( logger.isInfoEnabled() ).thenReturn( true );
        executionEventLogger = new ExecutionEventLogger( logger );

        ExecutionEvent event = mock( ExecutionEvent.class );
        MavenProject project = mock( MavenProject.class );
        when( project.getGroupId() ).thenReturn( "org.apache.maven" );
        when( project.getArtifactId() ).thenReturn( "maven-embedder" );
        when( project.getPackaging() ).thenReturn( "jar" );
        when( project.getName() ).thenReturn( "Apache Maven Embedder" );
        when( project.getVersion() ).thenReturn( "3.5.4-SNAPSHOT" );
        when( event.getProject() ).thenReturn( project );

        // execute
        executionEventLogger.projectStarted( event );

        // verify
        InOrder inOrder = inOrder( logger );
        inOrder.verify( logger ).info( "" );
        inOrder.verify( logger ).info( "------------------< org.apache.maven:maven-embedder >-------------------" );
        inOrder.verify( logger ).info( "Building Apache Maven Embedder 3.5.4-SNAPSHOT" );
        inOrder.verify( logger ).info( "--------------------------------[ jar ]---------------------------------" );
    }

    @Test
    public void testProjectStartedOverflow()
    {
        // prepare
        Logger logger = mock( Logger.class );
        when( logger.isInfoEnabled() ).thenReturn( true );
        executionEventLogger = new ExecutionEventLogger( logger );

        ExecutionEvent event = mock( ExecutionEvent.class );
        MavenProject project = mock( MavenProject.class );
        when( project.getGroupId() ).thenReturn( "org.apache.maven.plugins.overflow" );
        when( project.getArtifactId() ).thenReturn( "maven-project-info-reports-plugin" );
        when( project.getPackaging() ).thenReturn( "maven-plugin" );
        when( project.getName() ).thenReturn( "Apache Maven Project Info Reports Plugin" );
        when( project.getVersion() ).thenReturn( "3.0.0-SNAPSHOT" );
        when( event.getProject() ).thenReturn( project );

        // execute
        executionEventLogger.projectStarted( event );

        // verify
        InOrder inOrder = inOrder( logger );
        inOrder.verify( logger ).info( "" );
        inOrder.verify( logger ).info( "--< org.apache.maven.plugins.overflow:maven-project-info-reports-plugin >--" );
        inOrder.verify( logger ).info( "Building Apache Maven Project Info Reports Plugin 3.0.0-SNAPSHOT" );
        inOrder.verify( logger ).info( "----------------------------[ maven-plugin ]----------------------------" );
    }
}
