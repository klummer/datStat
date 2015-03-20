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
package org.labkey.datstat;

import com.drew.lang.annotations.Nullable;
import org.labkey.api.data.Container;
import org.labkey.api.module.Module;
import org.labkey.api.module.ModuleLoader;
import org.labkey.api.pipeline.PipelineJob;
import org.labkey.api.pipeline.PipelineJobException;
import org.labkey.api.security.User;
import org.labkey.api.security.permissions.AdminPermission;
import org.labkey.api.study.Study;
import org.labkey.api.study.StudyReloadSource;
import org.labkey.api.view.ActionURL;

/**
 * Created by klum on 2/12/2015.
 */
public class DatStatReloadSource implements StudyReloadSource
{
    public static final String NAME = "DatStat";

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public boolean isEnabled(Container container)
    {
        Module module = ModuleLoader.getInstance().getModule(DatStatModule.NAME);
        return container.getActiveModules().contains(module);
    }

    @Override
    public ActionURL getManageAction(Container c, User user)
    {
        if (c.hasPermission(user, AdminPermission.class))
            return new ActionURL(DatStatController.ConfigureAction.class, c);
        return null;
    }

    @Override
    public void generateReloadSource(@Nullable PipelineJob job, Study study) throws PipelineJobException
    {
        // do the export
        DatStatExport dsExport = new DatStatExport(job, study);
        dsExport.exportSource();
    }
}
