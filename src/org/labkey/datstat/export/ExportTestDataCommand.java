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
import org.apache.http.client.HttpClient;
import org.labkey.api.module.Module;
import org.labkey.api.module.ModuleLoader;
import org.labkey.api.resource.Resource;
import org.labkey.api.util.PageFlowUtil;
import org.labkey.api.util.Pair;
import org.labkey.api.util.Path;
import org.labkey.datstat.DatStatModule;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by klum on 3/15/15.
 */
public class ExportTestDataCommand extends ExportDataCommand
{
    public ExportTestDataCommand(String url, String username, String password, String projectName)
    {
        super(url, username, password, projectName);
    }

    @Override
    public ExportDataResponse execute(HttpClient client)
    {
        Module module = ModuleLoader.getInstance().getModule(DatStatModule.NAME);
        Path path = Path.parse("testData/").append("testData.json");
        Resource r = module.getModuleResource(path);
        String testData = null;
        if (r.isFile())
        {
            try
            {
                testData = PageFlowUtil.getStreamContentsAsString(r.getInputStream());
            }
            catch (IOException ioe)
            {
                throw new RuntimeException(ioe);
            }
        }
        return new ExportDataResponse(testData, HttpStatus.SC_OK);
    }
}
