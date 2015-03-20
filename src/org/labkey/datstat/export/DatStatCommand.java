/*
 * Copyright (c) 2015 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labkey.datstat.export;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.util.EntityUtils;
import org.labkey.api.util.Pair;

import java.net.URI;
import java.util.List;

/**
 * Created by klum on 2/17/2015.
 */
public abstract class DatStatCommand<ResponseType extends DatStatResponse>
{
    String _url;
    String _command;
    private String _username;
    private String _password;
    private List<Pair<String, String>> _parameters;

    public DatStatCommand(String url, String command, String username, String password, List<Pair<String, String>> parameters)
    {
        _url = url;
        _command = command;
        _username = username;
        _password = password;
        _parameters = parameters;
    }

    public ResponseType execute(HttpClient client)
    {
        try
        {
            StringBuilder sb = new StringBuilder(_url);
            if (!_url.endsWith("/"))
                sb.append("/");
            sb.append(_command);

            URIBuilder uri = new URIBuilder(sb.toString());
            for (Pair<String, String> param : _parameters)
            {
                uri.setParameter(param.getKey(), param.getValue());
            }
            HttpGet get = new HttpGet(uri.build());

            AuthScope scope = new AuthScope(uri.getHost(), AuthScope.ANY_PORT, AuthScope.ANY_REALM);
            BasicCredentialsProvider provider = new BasicCredentialsProvider();
            Credentials credentials = new UsernamePasswordCredentials(_username, _password);
            provider.setCredentials(scope, credentials);

            HttpClientContext clientContext = HttpClientContext.create();
            clientContext.setCredentialsProvider(provider);
            get.addHeader(new BasicScheme().authenticate(credentials, get, clientContext));

            ResponseHandler<String> handler = new BasicResponseHandler();

            HttpResponse response = client.execute(get, clientContext);
            StatusLine status = response.getStatusLine();

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                return createResponse(handler.handleResponse(response), status.getStatusCode());
            else
            {
                EntityUtils.consume(response.getEntity());
                return createResponse(status.getReasonPhrase(), status.getStatusCode());
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    protected abstract ResponseType createResponse(String response, int statusCode);
}
