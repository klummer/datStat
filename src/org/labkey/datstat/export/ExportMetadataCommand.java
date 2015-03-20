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

import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;
import org.labkey.api.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by klum on 2/17/2015.
 */
public class ExportMetadataCommand extends DatStatCommand<ExportMetadataResponse>
{
    public ExportMetadataCommand(String url, String username, String password, String projectName)
    {
        super(url, "SubmissionData", username, password,
                ((List<Pair<String, String>>) Arrays.asList(new Pair<String, String>("DataType","schema"), new Pair<String, String>("Study", projectName))));
    }

    @Override
    protected ExportMetadataResponse createResponse(String response, int statusCode)
    {
        return new ExportMetadataResponse(response, statusCode);
    }

    public static class ExportSubmissionMetadataTestCase extends Assert
    {
        static final String TEST_RESPONSE = "{\n" +
                "  \"DataDictionary\": {\n" +
                "    \"Surveys\": [\n" +
                "      {\n" +
                "        \"Name\": \"SCH_PLAT02_History_of_ALL\",\n" +
                "        \"Variables\": {\n" +
                "          \"HA_DATE_DIAGNOSED\": {\n" +
                "               \"DataType\": \"LongText\",\n" +
                "               \"ScaleValues\": null\n" +
                "           },\n" +
                "          \"HA_FLOW_ASSESSED\": {\n" +
                "               \"DataType\": \"Integer\",\n" +
                "               \"ScaleValues\": null\n" +
                "           },\n" +
                "          \"HA_CYTOGENETICS\": {\n" +
                "               \"DataType\": \"Integer\",\n" +
                "               \"ScaleValues\": {\n" +
                "                   \"0\": \"Normal\",\n" +
                "                   \"1\": \"Abnormal\",\n" +
                "                   \"2\": \"No growth\",\n" +
                "                   \"3\": \"Not done\"\n" +
                "               }\n" +
                "           }\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}\n";

        @Test
        public void testCreateResponse() throws Exception
        {
            ExportMetadataCommand cmd = new ExportMetadataCommand("url", "username", "password", "PLAT 02");

            DatStatResponse resp = cmd.createResponse(TEST_RESPONSE, HttpStatus.SC_OK);
            List<Map<String, Object>> data = resp.loadData();

            assertTrue(data.size() == 1);
            Map<String, Object> survey = data.get(0);
            assertTrue(survey.get("Name").equals("SCH_PLAT02_History_of_ALL"));

            Object variables = survey.get("Variables");
            assertTrue(variables instanceof Map);

            Map varMap = (Map)variables;

            Map dateDiagnosed = (Map)varMap.get("HA_DATE_DIAGNOSED");
            assertTrue(dateDiagnosed.get("DataType").equals("LongText"));
            assertTrue(dateDiagnosed.get("ScaleValues") == null);

            Map flowAssessed = (Map)varMap.get("HA_FLOW_ASSESSED");
            assertTrue(flowAssessed.get("DataType").equals("Integer"));
            assertTrue(flowAssessed.get("ScaleValues") == null);

            Map cytogenetics = (Map)varMap.get("HA_CYTOGENETICS");
            assertTrue(cytogenetics.get("DataType").equals("Integer"));

            Map cytogeneticsScale = (Map)cytogenetics.get("ScaleValues");
            assertTrue(cytogeneticsScale.get("0").equals("Normal"));
            assertTrue(cytogeneticsScale.get("1").equals("Abnormal"));
            assertTrue(cytogeneticsScale.get("2").equals("No growth"));
            assertTrue(cytogeneticsScale.get("3").equals("Not done"));
        }
    }
}
