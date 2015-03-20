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

import org.labkey.api.util.Pair;

import java.util.Collections;

/**
 * Created by klum on 2/13/2015.
 */
public class ExportSubmissionData extends DatStatCommand<DatStatResponse>
{
    public ExportSubmissionData(String url, String username, String password)
    {
        super(url, "SubmissionData", username, password, Collections.singletonList(new Pair<String, String>("DataType", "new")));
    }

    @Override
    protected DatStatResponse createResponse(String response, int statusCode)
    {
        return new DatStatResponse(response, statusCode, null, null);
    }
}
