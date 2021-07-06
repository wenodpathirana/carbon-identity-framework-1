/*
 *
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.identity.mgt.endpoint.util.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.mgt.endpoint.util.IdentityManagementEndpointConstants;
import org.wso2.carbon.identity.mgt.endpoint.util.IdentityManagementEndpointUtil;
import org.wso2.carbon.identity.mgt.endpoint.util.IdentityManagementServiceUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status.Family;

public class ApiClient {

    private static final String AUTHORIZATION           = "Authorization";
    private static final String CLIENT                  = "Client ";
    private static final String X_HTTP_METHOD_OVERRIDE  = "X-HTTP-Method-Override";
    private static final String PATCH                   = "PATCH";
    private static final String GET                     = "GET";
    private static final String POST                    = "POST";
    private static final String PUT                     = "PUT";
    private static final String DELETE                  = "DELETE";

    private static final Log log = LogFactory.getLog(ApiClient.class);

    private Map<String, String> defaultHeaderMap = new HashMap<String, String>();
    private String basePath = IdentityManagementEndpointUtil.buildEndpointUrl(IdentityManagementEndpointConstants
            .UserInfoRecovery.RECOVERY_API_RELATIVE_PATH);
    private boolean debugging = false;
    private int connectionTimeout = 0;

    private Client httpClient;
    private ObjectMapper objectMapper;

    private int statusCode;
    private Map<String, List<String>> responseHeaders;

    private DateFormat dateFormat;

    public ApiClient() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
//        objectMapper.registerModule(new JodaModule());
        objectMapper.setDateFormat(ApiClient.buildDefaultDateFormat());

        dateFormat = ApiClient.buildDefaultDateFormat();
        rebuildHttpClient();
    }

    public static DateFormat buildDefaultDateFormat() {
        // Use RFC3339 format for date and datetime.
        // See http://xml2rfc.ietf.org/public/rfc/html/rfc3339.html#anchor14
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        // Use UTC as the default time zone.
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }

    /**
     * Build the Client used to make HTTP requests with the latest settings,
     * i.e. objectMapper and debugging.
     */
    public ApiClient rebuildHttpClient() {
        // Add the JSON serialization support to Jersey
        JacksonJsonProvider jsonProvider = new JacksonJsonProvider(objectMapper);
        DefaultClientConfig conf = new DefaultClientConfig();
        conf.getSingletons().add(jsonProvider);
        Client client = Client.create(conf);
        client.setFollowRedirects(false);
        if (debugging) {
            client.addFilter(new LoggingFilter());
        }
        this.httpClient = client;
        return this;
    }

    /**
     * Returns the current object mapper used for JSON serialization/deserialization.
     * <p>
     * Note: If you make changes to the object mapper, remember to set it back via
     * <code>setObjectMapper</code> in order to trigger HTTP client rebuilding.
     * </p>
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public ApiClient setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        // Need to rebuild the Client as it depends on object mapper.
        rebuildHttpClient();
        return this;
    }

    public Client getHttpClient() {
        return httpClient;
    }

    public ApiClient setHttpClient(Client httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public String getBasePath() {
        return basePath;
    }

    public ApiClient setBasePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    /**
     * Gets the status code of the previous request
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Gets the response headers of the previous request
     */
    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }


    /**
     * Check that whether debugging is enabled for this API client.
     */
    public boolean isDebugging() {
        return debugging;
    }

    /**
     * Enable/disable debugging for this API client.
     *
     * @param debugging To enable (true) or disable (false) debugging
     */
    public ApiClient setDebugging(boolean debugging) {
        this.debugging = debugging;
        // Need to rebuild the Client as it depends on the value of debugging.
        rebuildHttpClient();
        return this;
    }

    /**
     * Connect timeout (in milliseconds).
     */
    public int getConnectTimeout() {
        return connectionTimeout;
    }

    /**
     * Set the connect timeout (in milliseconds).
     * A value of 0 means no timeout, otherwise values must be between 1 and
     * {@link Integer#MAX_VALUE}.
     */
    public ApiClient setConnectTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        httpClient.setConnectTimeout(connectionTimeout);
        return this;
    }

    /**
     * Get the date format used to parse/format date parameters.
     */
    public DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * Set the date format used to parse/format date parameters.
     */
    public ApiClient setDateFormat(DateFormat dateFormat) {
        this.dateFormat = dateFormat;
        // Also set the date format for model (de)serialization with Date properties.
        this.objectMapper.setDateFormat((DateFormat) dateFormat.clone());
        // Need to rebuild the Client as objectMapper changes.
        rebuildHttpClient();
        return this;
    }

    /**
     * Parse the given string into Date object.
     */
    public Date parseDate(String str) {
        try {
            return dateFormat.parse(str);
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Format the given Date object into string.
     */
    public String formatDate(Date date) {
        return dateFormat.format(date);
    }

    /**
     * Format the given parameter object into string.
     */
    public String parameterToString(Object param) {
        if (param == null) {
            return "";
        } else if (param instanceof Date) {
            return formatDate((Date) param);
        } else if (param instanceof Collection) {
            StringBuilder b = new StringBuilder();
            for (Object o : (Collection<?>) param) {
                if (b.length() > 0) {
                    b.append(",");
                }
                b.append(String.valueOf(o));
            }
            return b.toString();
        } else {
            return String.valueOf(param);
        }
    }

    /*
      Format to {@code Pair} objects.
    */
    public List<Pair> parameterToPairs(String collectionFormat, String name, Object value) {
        List<Pair> params = new ArrayList<Pair>();

        // preconditions
        if (StringUtils.isEmpty(name) || value == null) {
            return params;
        }

        Collection<?> valueCollection;
        if (value instanceof Collection<?>) {
            valueCollection = (Collection<?>) value;
        } else {
            params.add(new Pair(name, parameterToString(value)));
            return params;
        }

        if (valueCollection.isEmpty()) {
            return params;
        }

        // get the collection format
        collectionFormat = (collectionFormat == null || collectionFormat.isEmpty() ? "csv" : collectionFormat); // default: csv

        // create the params based on the collection format
        if (collectionFormat.equals("multi")) {
            for (Object item : valueCollection) {
                params.add(new Pair(name, parameterToString(item)));
            }

            return params;
        }

        String delimiter = ",";

        if (collectionFormat.equals("csv")) {
            delimiter = ",";
        } else if (collectionFormat.equals("ssv")) {
            delimiter = " ";
        } else if (collectionFormat.equals("tsv")) {
            delimiter = "\t";
        } else if (collectionFormat.equals("pipes")) {
            delimiter = "|";
        }

        StringBuilder sb = new StringBuilder();
        for (Object item : valueCollection) {
            sb.append(delimiter);
            sb.append(parameterToString(item));
        }

        params.add(new Pair(name, sb.substring(1)));

        return params;
    }

    /**
     * Check if the given MIME is a JSON MIME.
     * JSON MIME examples:
     * application/json
     * application/json; charset=UTF8
     * APPLICATION/JSON
     */
    public boolean isJsonMime(String mime) {
        return mime != null && mime.matches("(?i)application\\/json(;.*)?");
    }

    /**
     * Select the Accept header's value from the given accepts array:
     * if JSON exists in the given array, use it;
     * otherwise use all of them (joining into a string)
     *
     * @param accepts The accepts array to select from
     * @return The Accept header to use. If the given array is empty,
     * null will be returned (not to set the Accept header explicitly).
     */
    public String selectHeaderAccept(String[] accepts) {
        if (accepts.length == 0) {
            return null;
        }
        for (String accept : accepts) {
            if (isJsonMime(accept)) {
                return accept;
            }
        }
        return StringUtil.join(accepts, ",");
    }

    /**
     * Select the Content-Type header's value from the given array:
     * if JSON exists in the given array, use it;
     * otherwise use the first one of the array.
     *
     * @param contentTypes The Content-Type array to select from
     * @return The Content-Type header to use. If the given array is empty,
     * JSON will be used.
     */
    public String selectHeaderContentType(String[] contentTypes) {
        if (contentTypes.length == 0) {
            return "application/json";
        }
        for (String contentType : contentTypes) {
            if (isJsonMime(contentType)) {
                return contentType;
            }
        }
        return contentTypes[0];
    }

    /**
     * Escape the given string to be used as URL query value.
     */
    public String escapeString(String str) {
        try {
            return URLEncoder.encode(str, "utf8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    /**
     * Serialize the given Java object into string according the given
     * Content-Type (only JSON is supported for now).
     */
    public Object serialize(Object obj, String contentType, Map<String, Object> formParams) throws ApiException {
        if (contentType.startsWith("multipart/form-data")) {
            FormDataMultiPart mp = new FormDataMultiPart();
            for (Entry<String, Object> param : formParams.entrySet()) {
                if (param.getValue() instanceof File) {
                    File file = (File) param.getValue();
                    mp.bodyPart(new FileDataBodyPart(param.getKey(), file, MediaType.MULTIPART_FORM_DATA_TYPE));
                } else {
                    mp.field(param.getKey(), parameterToString(param.getValue()), MediaType.MULTIPART_FORM_DATA_TYPE);
                }
            }
            return mp;
        } else if (contentType.startsWith("application/x-www-form-urlencoded")) {
            return this.getXWWWFormUrlencodedParams(formParams);
        } else {
            // We let Jersey attempt to serialize the body
            return obj;
        }
    }

    /**
     * Build full URL by concatenating base path, the given sub path and query parameters.
     *
     * @param path        The sub path
     * @param queryParams The query parameters
     * @return The full URL
     */
    private String buildUrl(String path, List<Pair> queryParams) {
        final StringBuilder url = new StringBuilder();
        url.append(basePath).append(path);

        if (queryParams != null && !queryParams.isEmpty()) {
            // support (constant) query string in `path`, e.g. "/posts?draft=1"
            String prefix = path.contains("?") ? "&" : "?";
            for (Pair param : queryParams) {
                if (param.getValue() != null) {
                    if (prefix != null) {
                        url.append(prefix);
                        prefix = null;
                    } else {
                        url.append("&");
                    }
                    String value = parameterToString(param.getValue());
                    url.append(escapeString(param.getName())).append("=").append(escapeString(value));
                }
            }
        }

        return url.toString();
    }

    private ClientResponse getAPIResponse(String path,
                                          String method,
                                          List<Pair> queryParams,
                                          Object body,
                                          Map<String, String> headerParams,
                                          Map<String, Object> formParams,
                                          String accept,
                                          String contentType,
                                          String[] authNames) throws ApiException {

        if (body != null && !formParams.isEmpty()) {
            throw new ApiException(500, "Cannot have body and form params");
        }

        if (!isRequestMethodSupported(method)) {
            throw new ApiException(500, "unknown method type " + method);
        }

        final String url = buildUrl(path, queryParams);

        if (log.isDebugEnabled()) {
            String headerParamsAsString = StringUtils.EMPTY;
            if (MapUtils.isNotEmpty(headerParams)) {
                headerParamsAsString = headerParams.keySet().stream()
                        .map(key -> key + "=" + headerParams.get(key))
                        .collect(Collectors.joining(", ", "{", "}"));
            }

            String formParamsAsString = StringUtils.EMPTY;
            if (MapUtils.isNotEmpty(formParams)) {
                formParamsAsString = formParams.keySet().stream()
                        .map(key -> key + "=" + formParams.get(key))
                        .collect(Collectors.joining(", ", "{", "}"));
            }

            log.debug(
                    String.format(
                            "Initiating api request for " +
                                    "\npath=%s, " +
                                    "\nmethod=%s, " +
                                    "\nheaderParams=%s, " +
                                    "\nformParams=%s, " +
                                    "\naccept=%s, " +
                                    "\ncontentType=%s",
                            url, method, headerParamsAsString, formParamsAsString, accept, contentType
                    )
            );
        }

        Builder builder;
        if (accept == null) {
            builder = httpClient.resource(url).getRequestBuilder();
        } else {
            builder = httpClient.resource(url).accept(accept);
        }

        for (Entry<String, String> entry : headerParams.entrySet()) {
            builder = builder.header(entry.getKey(), entry.getValue());
        }
        for (Entry<String, String> entry : defaultHeaderMap.entrySet()) {
            if (!headerParams.containsKey(entry.getKey())) {
                builder = builder.header(entry.getKey(), entry.getValue());
            }
        }

        String toEncode = IdentityManagementServiceUtil.getInstance().getAppName() + ":"
                + String.valueOf(IdentityManagementServiceUtil.getInstance().getAppPassword());
        byte[] encoding = Base64.encodeBase64(toEncode.getBytes());
        String authHeader = new String(encoding, Charset.defaultCharset());

        try {
            ClientResponse response = null;
            if (GET.equals(method)) {
                response = builder.header(AUTHORIZATION,  CLIENT + authHeader).get(ClientResponse.class);
            } else if (POST.equals(method)) {
                response = builder.type(contentType).header(AUTHORIZATION, CLIENT + authHeader).post(ClientResponse.class,
                        serialize(body, contentType, formParams));
            } else if (PUT.equals(method)) {
                response = builder.type(contentType).header(AUTHORIZATION, CLIENT + authHeader).put(ClientResponse.class,
                        serialize(body, contentType, formParams));
            } else if (DELETE.equals(method)) {
                response = builder.type(contentType).header(AUTHORIZATION, CLIENT + authHeader).delete
                        (ClientResponse.class, serialize(body, contentType, formParams));
            } else if (PATCH.equals(method)) {
                response = builder.type(contentType).header(X_HTTP_METHOD_OVERRIDE, PATCH).header
                        (AUTHORIZATION, CLIENT + authHeader).post
                        (ClientResponse.class, serialize(body, contentType, formParams));
            }

            if (log.isDebugEnabled()) {
                if (response != null) {
                    log.debug(String.format("Response from the %s request made to url %s " +
                            "\nResponse: status=%s, statusMessage=%s",
                            method, url, response.getStatusInfo().getStatusCode(),
                            response.getStatusInfo().getReasonPhrase()));
                } else {
                    log.debug(String.format("Response from the %s request made to url %s is null.",
                            method, url ));
                }
            }
            return response;
        } catch (Exception e) {
            String errMsg = String.format("Error while performing the request method: %s on the " +
                    "resource: %s", method, url) ;
            log.error(errMsg, e);
            throw new ApiException(500, "Error while accessing the backend service");
        }
    }

    /**
     * Invoke API by sending HTTP request with the given options.
     *
     * @param path         The sub-path of the HTTP URL
     * @param method       The request method, one of "GET", "POST", "PUT", and "DELETE"
     * @param queryParams  The query parameters
     * @param body         The request body object - if it is not binary, otherwise null
     * @param headerParams The header parameters
     * @param formParams   The form parameters
     * @param accept       The request's Accept header
     * @param contentType  The request's Content-Type header
     * @param authNames    The authentications to apply
     * @return The response body in type of string
     */
    public <T> T invokeAPI(String path, String method, List<Pair> queryParams, Object body, Map<String, String> headerParams, Map<String, Object> formParams, String accept, String contentType, String[] authNames, GenericType<T> returnType) throws ApiException {

        ClientResponse response = getAPIResponse(path, method, queryParams, body, headerParams, formParams, accept, contentType, authNames);

        statusCode = response.getStatusInfo().getStatusCode();
        responseHeaders = response.getHeaders();

        if (response.getStatusInfo().getStatusCode() == ClientResponse.Status.NO_CONTENT.getStatusCode()) {

            throw new ApiException(204, "No content Found");

        } else if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
            if (returnType != null) {
                if (response.hasEntity()) {
                    return response.getEntity(returnType);
                }
            }
            return null;
        } else {
            String message = "error";
            String respBody = null;
            if (response.hasEntity()) {
                try {
                    respBody = response.getEntity(String.class);
                    message = respBody;
                } catch (RuntimeException e) {
                    // e.printStackTrace();
                }
            }
            throw new ApiException(
                    response.getStatusInfo().getStatusCode(),
                    message,
                    response.getHeaders(),
                    respBody);
        }
    }

    private boolean isRequestMethodSupported(String method) {

        return  GET.equals(method) || POST.equals(method) || PUT.equals(method) || DELETE.equals(method)
                || PATCH.equals(method);
    }


    /**
     * Encode the given form parameters as request body.
     */
    private String getXWWWFormUrlencodedParams(Map<String, Object> formParams) {
        StringBuilder formParamBuilder = new StringBuilder();

        for (Entry<String, Object> param : formParams.entrySet()) {
            String valueStr = parameterToString(param.getValue());
            try {
                formParamBuilder.append(URLEncoder.encode(param.getKey(), "utf8"))
                        .append("=")
                        .append(URLEncoder.encode(valueStr, "utf8"));
                formParamBuilder.append("&");
            } catch (UnsupportedEncodingException e) {
                // move on to next
            }
        }

        String encodedFormParams = formParamBuilder.toString();
        if (encodedFormParams.endsWith("&")) {
            encodedFormParams = encodedFormParams.substring(0, encodedFormParams.length() - 1);
        }

        return encodedFormParams;
    }
}
