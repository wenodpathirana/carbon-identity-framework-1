/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.application.authentication.endpoint.util;

public class Constants {

    public static final String SESSION_DATA_KEY = "sessionDataKey";
    public static final String SESSION_DATA_KEY_CONSENT = "sessionDataKeyConsent";
    public static final String AUTH_FAILURE = "authFailure";
    public static final String ERROR_CODE = "errorCode";
    public static final String ERROR_MSG = "errorMsg";
    public static final String AUTH_FAILURE_MSG = "authFailureMsg";
    public static final String STATUS = "status";
    public static final String STATUS_MSG = "statusMsg";
    public static final String IDP_AUTHENTICATOR_MAP = "idpAuthenticatorMap";
    public static final String RESIDENT_IDP_RESERVED_NAME = "LOCAL";
    public static final String MISSING_CLAIMS = "missingClaims";
    public static final String REQUESTED_CLAIMS = "requestedClaims";
    public static final String MANDATORY_CLAIMS = "mandatoryClaims";
    public static final String USER_CLAIMS_CONSENT_ONLY = "userClaimsConsentOnly";
    public static final String CLAIM_SEPARATOR = ",";
    public static final String REQUEST_PARAM_SP = "sp";
    // Response Messages
    public static final String ACCOUNT_RESEND_SUCCESS_RESOURCE = "account.resend.email.success" ;
    public static final String ACCOUNT_RESEND_FAIL_RESOURCE = "account.resend.email.fail" ;
    public static final String CONFIGURATION_ERROR = "configuration.error";
    public static final String AUTHENTICATION_MECHANISM_NOT_CONFIGURED = "authentication.mechanism.not.configured";
    public static final String ERROR_WHILE_BUILDING_THE_ACCOUNT_RECOVERY_ENDPOINT_URL =
            "Error.while.building.the.account.recovery.endpoint.url";
    // WebAPP Configurations
    public static final String ACCOUNT_RECOVERY_REST_ENDPOINT_URL = "AccountRecoveryRESTEndpointURL";
    public static final String ENABLE_AUTHENTICATION_WITH_REST_API = "EnableAuthenticationWithAuthenticationRESTAPI";
    public static final String AUTHENTICATION_REST_ENDPOINT_URL = "AuthenticationRESTEndpointURL";


    public static final String HTTPS_URL = "https://";
    public static final String HOST = "identity.server.host";
    public static final String COLON = ":";
    public static final String PORT = "identity.server.port";
    public static final String SERVICES_URL = "identity.server.serviceURL";
    public static final String DASHBOARD_RELYING_PARTY = "wso2.my.dashboard";
    public static final String CONFIG_APP_NAME = "app.name";
    public static final String CONFIG_APP_PASSWORD = "app.password";
    public static final String CONFIG_SERVER_ORIGIN = "identity.server.origin";

    private Constants() {
    }

    public static class SAML2SSO {
        public static final String ASSERTION_CONSUMER_URL = "assertnConsumerURL";
        public static final String RELAY_STATE = "RelayState";
        public static final String SAML_RESP = "SAMLResponse";

        private SAML2SSO() {
        }
    }

    public static class TenantConstants {

        // Tenant list dropdown related properties

        public static final String USERNAME = "mutual.ssl.username";
        public static final String USERNAME_HEADER = "username.header";
        public static final String CLIENT_KEY_STORE = "client.keyStore";
        public static final String CLIENT_TRUST_STORE = "client.trustStore";
        public static final String CLIENT_KEY_STORE_PASSWORD = "Carbon.Security.KeyStore.Password";
        public static final String CLIENT_TRUST_STORE_PASSWORD = "Carbon.Security.TrustStore.Password";
        public static final String HOSTNAME_VERIFICATION_ENABLED = "hostname.verification.enabled";
        public static final String KEY_MANAGER_TYPE = "key.manager.type";
        public static final String TRUST_MANAGER_TYPE = "trust.manager.type";
        public static final String TENANT_LIST_ENABLED = "tenantListEnabled";
        public static final String MUTUAL_SSL_MANAGER_ENABLED = "mutualSSLManagerEnabled";
        public static final String TLS_PROTOCOL = "tls.protocol";

        // Service URL constants
        public static final String TENANT_MGT_ADMIN_SERVICE_URL = "/TenantMgtAdminService/retrieveTenants";

        // String constants for SOAP response processing
        public static final String RETURN = "return";
        public static final String RETRIEVE_TENANTS_RESPONSE = "retrieveTenantsResponse";
        public static final String TENANT_DOMAIN = "tenantDomain";
        public static final String ACTIVE = "active";
        public static final String TENANT_DATA_SEPARATOR = ",";
        public static final String RELATIVE_PATH_START_CHAR = ".";
        public static final String CHARACTER_ENCODING = "UTF-8";
        public static final String CONFIG_RELATIVE_PATH = "./repository/conf/identity/EndpointConfig.properties";
        public static final String IDENTITY_XML_RELATIVE_PATH = "./repository/conf/identity/identity.xml";
        public static final String CONFIG_FILE_NAME = "EndpointConfig.properties";

        private TenantConstants() {
        }
    }

    public static class UserRegistrationConstants {

        public static final String WSO2_DIALECT = "http://wso2.org/claims";
        public static final String FIRST_NAME = "First Name";
        public static final String LAST_NAME = "Last Name";
        public static final String EMAIL_ADDRESS = "Email";
        public static final String USER_REGISTRATION_SERVICE = "/UserRegistrationAdminService" +
                ".UserRegistrationAdminServiceHttpsSoap11Endpoint/";

        private UserRegistrationConstants() {

        }
    }

    public static class ErrorToi18nMappingConstants {

        public static final String INVALID_CALLBACK_CALLBACK_NOT_MATCH = "invalid_callback_callback.not.match";
        public static final String INVALID_CALLBACK_CALLBACK_NOT_MATCH_I18N_KEY = "callback.not.match";
        public static final String INVALID_CLIENT_APP_NOT_FOUND = "invalid_client_application.not.found";
        public static final String INVALID_CLIENT_APP_NOT_FOUND_I18N_KEY = "application.not.found";
        public static final String INVALID_REQUEST_INVALID_REDIRECT_URI = "invalid_request_invalid.redirect.uri";
        public static final String INVALID_REQUEST_INVALID_REDIRECT_URI_I18N_KEY = "invalid.redirect.uri";
        public static final String AUTHENTICATION_ATTEMPT_FAILED_AUTHORIZATION_FAILED = "authentication.attempt.failed_authorization.failed";
        public static final String AUTHENTICATION_ATTEMPT_FAILED_AUTHORIZATION_FAILED_I18N_KEY = "authentication.attempt.failed";
        public static final String MISCONFIGURATION_ERROR_SOMETHING_WENT_WRONG_CONTACT_ADMIN = "misconfiguration.error_something.went.wrong.contact.admin";
        public static final String MISCONFIGURATION_ERROR_SOMETHING_WENT_WRONG_CONTACT_ADMIN_I18N_KEY = "misconfiguration.error";
        public static final String SUSPICIOUS_AUTHENTICATION_ATTEMPTS_SUSPICIOUS_AUTHENTICATION_ATTEMPTS_DESCRIPTION = "suspicious.authentication.attempts_suspicious.authentication.attempts.description";
        public static final String SUSPICIOUS_AUTHENTICATION_ATTEMPTS_SUSPICIOUS_AUTHENTICATION_ATTEMPTS_DESCRIPTION_I18N_KEY = "suspicious.authentication.attempts";
        public static final String INCORRECT_ERROR_MAPPING_KEY = "incorrect.error.mapping";

        private ErrorToi18nMappingConstants() {

        }
    }
}
