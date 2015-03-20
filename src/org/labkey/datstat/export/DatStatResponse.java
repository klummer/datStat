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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.labkey.api.pipeline.PipelineJob;
import org.labkey.api.reader.DataLoader;
import org.labkey.api.reader.TabLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.labkey.api.util.JsonUtil.expectArrayStart;

/**
 * Created by klum on 2/13/2015.
 */
public class DatStatResponse
{
    protected String _text;
    protected String _dataNodeName;
    protected String _dataSubNodeName;
    protected int _statusCode;
    protected JsonParser _parser;

    protected int _totalRecords;

    public DatStatResponse(String text, int statusCode, String dataNodeName, String dataSubNodeName)
    {
        _text = text;
        _statusCode = statusCode;
        _dataNodeName = dataNodeName;
        _dataSubNodeName = dataSubNodeName;
    }

    public List<Map<String, Object>> loadData()
    {
        List<Map<String, Object>> data = new ArrayList<>();
        try {

            JsonFactory factory = new JsonFactory();
            new ObjectMapper(factory);
            _parser = factory.createParser(_text);

            // locate the data array
            if (!ensureDataNode(_parser))
            {
/*
                if (_job != null)
                    _job.error("Unable to locate data in the returned response: " + _text);
*/
                throw new IOException("Unable to locate data in the returned response: " + _text);
            }

            // parse the data array
            parseDataArray(_parser, data);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return data;
    }

    /**
     * Position the parser to the start of the data array object
     * @param parser
     * @return
     * @throws IOException
     */
    protected boolean ensureDataNode(JsonParser parser) throws IOException
    {
        if (findDataNode(parser, _dataNodeName) && findDataNode(parser, _dataSubNodeName))
        {
            // find the start of the array
            JsonToken token = parser.getCurrentToken();
            while (token != JsonToken.END_OBJECT && token != JsonToken.END_ARRAY)
            {
                token = parser.nextToken();
                if (token == JsonToken.START_ARRAY)
                    return true;
            }
        }
        return false;
    }

    /**
     * Locates the JSON node with the specified field name
     * @param parser
     * @return
     * @throws IOException
     */
    protected boolean findDataNode(JsonParser parser, String nodeName) throws IOException
    {
        JsonToken token = parser.nextToken();
        while (token != JsonToken.END_OBJECT && token != JsonToken.END_ARRAY)
        {
            token = parser.nextToken();
            if (token == JsonToken.FIELD_NAME)
            {
                String fieldName = parser.getCurrentName();
                if (nodeName.equals(fieldName))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Parse the array of objects into a list of row maps
     * @param parser
     * @param data
     */
    protected void parseDataArray(JsonParser parser, List<Map<String, Object>> data) throws IOException
    {
        expectArrayStart(parser);
        JsonToken token = parser.nextToken();

        while (token != JsonToken.END_ARRAY)
        {
            if (token == JsonToken.END_OBJECT)
                break;

            Map node = _parser.readValueAs(Map.class);
            Map<String, Object> row = new HashMap<>();
            for (Object key : node.keySet())
            {
                String fieldName = String.valueOf(key);
                row.put(fieldName, node.get(key));
            }
            data.add(row);
            token = _parser.nextToken();

            if (token == JsonToken.END_ARRAY)
                break;
        }
    }

    public String getText()
    {
        return _text;
    }

    public int getStatusCode()
    {
        return _statusCode;
    }
}
