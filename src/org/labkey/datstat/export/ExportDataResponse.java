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

/**
 * Created by klum on 2/24/2015.
 * Response handler designed to consume a JSON response with the following shape:
 *
 * SubmissionData : {
 *     Surveys : [
 *      {
 *          Name : 'PLAT02',
 *          Data : [
 *              HA_DATE_DIAGNOSED : '12/22/2014 3:32:50 PM',
 *              VISIT_ID : '3000'
 *          }
 *      },{
 *          Name : 'PLAT03',
 *          Variables : [
 *              HA_DATE_DIAGNOSED : '12/22/2014 3:32:50 PM',
 *              VISIT_ID : '3000'
 *          }
 *      }
 *     ]
 */
public class ExportDataResponse extends DatStatResponse
{
    public ExportDataResponse(String response, int statusCode)
    {
        super(response, statusCode, "SubmissionData", "Surveys");
    }
}
