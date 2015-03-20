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
 * Created by klum on 2/24/2015.
 */
public class ExportDataCommand extends DatStatCommand<ExportDataResponse>
{
    public ExportDataCommand(String url, String username, String password, String projectName)
    {
        super(url, "SubmissionData", username, password,
                ((List<Pair<String, String>>) Arrays.asList(new Pair<String, String>("DataType", "new"), new Pair<String, String>("Study", projectName))));
    }

    @Override
    protected ExportDataResponse createResponse(String response, int statusCode)
    {
        return new ExportDataResponse(response, statusCode);
    }

    public static class ExportSubmissionDataTestCase extends Assert
    {
        static final String TEST_RESPONSE = "{\n" +
                "  \"SubmissionData\": {\n" +
                "    \"Surveys\": [\n" +
                "      {\n" +
                "        \"Name\": \"SCH_PLAT02_History_of_ALL\",\n" +
                "        \"Data\": [{\n" +
                "          \"HA_DATE_DIAGNOSED\": \"12/22/2014 3:32:50 PM\",\n" +
                "          \"HA_FLOW_ASSESSED\": \"1033\",\n" +
                "          \"HA_ABNORM_CYTO.PH_\": \"True\",\n" +
                "          \"HA_ABNORM_CYTO.HYPO.TEXT\": \"1033\",\n" +
                "          \"HA_ABNORM_CYTO.MLL\": \"True\",\n" +
                "          \"HA_ABNORM_CYTO.MLL.TEXT2\": \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0\",\n" +
                "          \"HA_CNS_STATUS\": \"1033\",\n" +
                "          \"SUBJENR_PTINIT\": \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0\",\n" +
                "          \"VISIT_ID\": \"1\",\n" +
                "          \"DATSTAT.SITE\": \"f3c7a360-68e1-4501-8e69-b263ead0ea69\",\n" +
                "          \"DATSTAT.RMSINSTANCE\": \"1\",\n" +
                "          \"DATSTAT.SIGNATURE\": \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0\"\n" +
                "        },{\n" +
                "          \"HA_DATE_DIAGNOSED\": \"12/22/2014 3:32:50 PM\",\n" +
                "          \"HA_FLOW_ASSESSED\": \"1033\",\n" +
                "          \"HA_ABNORM_CYTO.PH_\": \"True\",\n" +
                "          \"HA_ABNORM_CYTO.HYPO.TEXT\": \"1033\",\n" +
                "          \"HA_ABNORM_CYTO.MLL\": \"True\",\n" +
                "          \"HA_ABNORM_CYTO.MLL.TEXT2\": \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0\",\n" +
                "          \"HA_CNS_STATUS\": \"1033\",\n" +
                "          \"SUBJENR_PTINIT\": \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0\",\n" +
                "          \"VISIT_ID\": \"1\",\n" +
                "          \"DATSTAT.SITE\": \"f3c7a360-68e1-4501-8e69-b263ead0ea69\",\n" +
                "          \"DATSTAT.RMSINSTANCE\": \"1\",\n" +
                "          \"DATSTAT.SIGNATURE\": \"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0\"\n" +
                "        }]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}\n";

        @Test
        public void testCreateResponse() throws Exception
        {
            ExportDataCommand cmd = new ExportDataCommand("url", "username", "password", "PLAT 02");

            DatStatResponse resp = cmd.createResponse(TEST_RESPONSE, HttpStatus.SC_OK);
            List<Map<String, Object>> data = resp.loadData();

            assertTrue(data.size() == 1);
            Map<String, Object> survey = data.get(0);
            assertTrue(survey.get("Name").equals("SCH_PLAT02_History_of_ALL"));

            Object rowdata = survey.get("Data");
            assertTrue(rowdata instanceof List);

            List rows = (List)rowdata;
            for (Object row : rows)
            {
                if (row instanceof Map)
                {
                    Map rowMap = (Map)row;

                    assertTrue(rowMap.get("HA_DATE_DIAGNOSED").equals("12/22/2014 3:32:50 PM"));
                    assertTrue(rowMap.get("HA_FLOW_ASSESSED").equals("1033"));
                    assertTrue(rowMap.get("HA_ABNORM_CYTO.PH_").equals("True"));
                    assertTrue(rowMap.get("HA_ABNORM_CYTO.HYPO.TEXT").equals("1033"));
                    assertTrue(rowMap.get("HA_ABNORM_CYTO.MLL").equals("True"));
                    assertTrue(rowMap.get("HA_ABNORM_CYTO.MLL.TEXT2").equals("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0"));
                    assertTrue(rowMap.get("HA_CNS_STATUS").equals("1033"));
                }
            }
        }
    }
}
