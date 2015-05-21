/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.fortress.realm.tomcat;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.realm.RealmBase;

import java.security.Principal;
import java.util.logging.Logger;

/**
 * This class extends the Tomcat 7 and beyond RealmBase class and provides Java EE security services within the Tomcat container.
 * This class is a "proxy" for the {@link org.apache.directory.fortress.realm.tomcat.TcAccessMgrImpl} class which isolates dependencies from the Tomcat
 * runtime environment by loading the implementation on a URLClassLoader.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class TC7AccessMgrFascade extends RealmBase
{
    private static final String CLS_NM = TC7AccessMgrFascade.class.getName();
    private static final Logger LOG = Logger.getLogger( CLS_NM );
    private static final String REALM_IMPL = "org.apache.directory.fortress.realm.tomcat.TcAccessMgrImpl";
    private static final String REALM_CLASSPATH = "REALM_CLASSPATH";
    private static final String JBOSS_AGENT = "jboss";
    private String container = "Catalina7";
    private String defaultRoles;
    private String realmClasspath;
    private TcAccessMgr realm = new TcAccessMgrImpl();

    /**
     * Gets the info attribute of the TcAccessMgrProxy object
     *
     * @return The info value
     */
    @Override
    public String getInfo()
    {
        return info;
    }


    /**
     * Perform user authentication and evaluate password policies.
     *
     * @param userId   Contains the userid of the user signing on.
     * @param password Contains the user's password.
     * @return Principal whic     * This method will load the Fortress Tomcat implementation on a URL classloader.  Methods on the implementation are
     * wrapped by methods on this class and are accessed via the {@code realm} instance variable of this class.
     */
    @Override
    public Principal authenticate( String userId, String password )
    {
        if ( realm == null )
        {
            throw new RuntimeException( CLS_NM + "authenticate detected Fortress Tomcat7 Realm not initialized correctly.  Check your Fortress Realm configuration" );
        }
        
        return realm.authenticate( userId, password.toCharArray()) ;
    }


    /**
     * Determine if given Role is contained within User's Tomcat Principal object.  This method does not need to hit
     * the ldap server as the User's activated Roles are loaded into {@link org.apache.directory.fortress.realm.tomcat.TcPrincipal#setContext(java.util.HashMap)}
     *
     * @param principal Contains User's Tomcat RBAC Session data that includes activated Roles.
     * @param role  Maps to {@code org.apache.directory.fortress.core.rbac.Role#name}.
     * @return True if Role is found in TcPrincipal, false otherwise.
     */
    @Override
    public boolean hasRole( Wrapper wrapper, Principal principal, String role )
    {
        if ( realm == null )
        {
            throw new RuntimeException( CLS_NM + "authenticate detected Fortress Tomcat7 Realm not initialized correctly.  Check your Fortress Realm configuration" );
        }
        
        return realm.hasRole( principal, role );
    }
    

    /**
     * Gets the name attribute of the TcAccessMgrProxy object
     *
     * @return The name value
     */
    @Override
    protected String getName()
    {
        return ( CLS_NM );
    }


    /**
     * Gets the password attribute of the TcAccessMgrProxy object
     *
     * @param username Description of the Parameter
     * @return The password value
     */
    @Override
    protected String getPassword( String username )
    {
        return null;
    }


    /**
     * Gets the principal attribute of the TcAccessMgrProxy object
     *
     * @param username Description of the Parameter
     * @return The principal value
     */
    @Override
    protected Principal getPrincipal( String username )
    {
        return null;
    }


    /**
     * Prepare for the beginning of active use of the public methods of this
     * component and implement the requirements of
     * {@link org.apache.catalina.util.LifecycleBase#startInternal()}.
     *
     * @throws LifecycleException if this component detects a fatal error
     *                            that prevents this component from being used
     */
    @Override
    protected void startInternal() throws LifecycleException
    {
        super.startInternal();
    }

    
    /**
     * Gracefully terminate the active use of the public methods of this
     * component and implement the requirements of
     * {@link org.apache.catalina.util.LifecycleBase#stopInternal()}.
     *
     * @throws LifecycleException if this component detects a fatal error
     *                            that needs to be reported
     */
    @Override
    protected void stopInternal() throws LifecycleException
    {

        // Perform normal superclass finalization
        super.stopInternal();

        // Release reference to our sentry impl
        realm = null;

    }

    
    /**
     * Gets the containerType attribute of the TcAccessMgrProxy object
     *
     * @return The containerType value
     */
    public String getContainerType()
    {
        return container;
    }

    
    /**
     * Sets the containerType attribute of the TcAccessMgrProxy object
     *
     * @param container The new containerType value
     */
    public void setContainerType( String container )
    {
        LOG.info( CLS_NM + ".setContainerType <" + container + ">" );
        this.container = container;
    }
    

    /**
     * Gets the realmClasspath attribute of the TcAccessMgrProxy object
     *
     * @return The realmClasspath value
     */
    public String getRealmClasspath()
    {
        LOG.info( CLS_NM + ".getRealmClasspath <" + realmClasspath + ">" );
        return realmClasspath;
    }
    

    /**
     * Sets the realmClasspath attribute of the TcAccessMgrProxy object
     *
     * @param rCpth The new realmClasspath value
     */
    public void setRealmClasspath( String rCpth )
    {
        LOG.info( CLS_NM + ".setRealmClasspath <" + rCpth + ">" );
        this.realmClasspath = rCpth;
    }

    /**
     * Gets the defaultRoles attribute of the TcAccessMgrProxy object.  When set, it will be passed into all subsequent calls to Fortress createSession.
     *
     * @return String containing comma delimited list of role names.
     */
    public String getDefaultRoles()
    {
        LOG.info( CLS_NM + ".getDefaultRoles <" + defaultRoles + ">" );
        return defaultRoles;
    }

    /**
     * Sets the defaultRoles attribute of the TcAccessMgrProxy object.  When set, it will be passed into all subsequent calls to Fortress createSession.
     *
     * @param defaultRoles containing comma delimited list of role names.
     */
    public void setDefaultRoles( String defaultRoles )
    {
        LOG.info( CLS_NM + ".setDefaultRoles <" + defaultRoles + ">"  );
        this.defaultRoles = defaultRoles;
    }
}