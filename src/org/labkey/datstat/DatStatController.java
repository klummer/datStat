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

import org.apache.xmlbeans.XmlException;
import org.labkey.api.action.ApiAction;
import org.labkey.api.action.ApiResponse;
import org.labkey.api.action.ApiSimpleResponse;
import org.labkey.api.action.SimpleViewAction;
import org.labkey.api.action.SpringActionController;
import org.labkey.api.data.PropertyManager;
import org.labkey.api.pipeline.PipelineJob;
import org.labkey.api.pipeline.PipelineService;
import org.labkey.api.pipeline.PipelineUrls;
import org.labkey.api.pipeline.PipelineValidationException;
import org.labkey.api.security.Encryption;
import org.labkey.api.security.RequiresPermissionClass;
import org.labkey.api.security.permissions.AdminPermission;
import org.labkey.api.study.StudyReloadSource;
import org.labkey.api.study.StudyService;
import org.labkey.api.study.StudyUrls;
import org.labkey.api.util.PageFlowUtil;
import org.labkey.api.util.XmlBeansUtil;
import org.labkey.api.view.HtmlView;
import org.labkey.api.view.JspView;
import org.labkey.api.view.NavTree;
import org.labkey.study.xml.datStatExport.DatStatConfigDocument;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

public class DatStatController extends SpringActionController
{
    private static final DefaultActionResolver _actionResolver = new DefaultActionResolver(DatStatController.class);
    public static final String NAME = "datstat";


    public DatStatController()
    {
        setActionResolver(_actionResolver);
    }

    @RequiresPermissionClass(AdminPermission.class)
    public class ConfigureAction extends SimpleViewAction<DatStatManager.DatStatSettings>
    {
        @Override
        public ModelAndView getView(DatStatManager.DatStatSettings form, BindException errors) throws Exception
        {
            if (Encryption.isMasterEncryptionPassPhraseSpecified())
            {
                form = DatStatManager.get().getDatStatSettings(getViewContext());
                return new JspView<>("/org/labkey/datstat/view/configure.jsp", form, errors);
            }
            else
            {
                return new HtmlView("<span class='labkey-error'>Unable to save or retrieve configuration information, MasterEncryptionKey has not been specified in labkey.xml.</span>");
            }
        }

        @Override
        public NavTree appendNavTrail(NavTree root)
        {
            return root.addChild("DATStat Configuration");
        }
    }

    @RequiresPermissionClass(AdminPermission.class)
    public class SaveDatStatConfig extends ApiAction<DatStatManager.DatStatSettings>
    {
        @Override
        public void validateForm(DatStatManager.DatStatSettings form, Errors errors)
        {
            // validate any metadata
            String metadata = form.getMetadata();
            if (metadata != null)
            {
                try
                {
                    DatStatConfigDocument.Factory.parse(metadata, XmlBeansUtil.getDefaultParseOptions());
                }
                catch (XmlException e)
                {
                    errors.reject(ERROR_MSG, "The metadata submitted was malformed. The following error was returned : " + e.getMessage());
                }
            }
        }

        @Override
        public ApiResponse execute(DatStatManager.DatStatSettings form, BindException errors) throws Exception
        {
            ApiSimpleResponse response = new ApiSimpleResponse();
            PropertyManager.PropertyMap map = PropertyManager.getEncryptedStore().getWritableProperties(getContainer(), DatStatManager.DATSTAT_PROPERTIES, true);

            map.put(DatStatManager.DatStatSettings.Options.url.name(), form.getBaseServerUrl());
            map.put(DatStatManager.DatStatSettings.Options.user.name(), form.getUsername());
            map.put(DatStatManager.DatStatSettings.Options.password.name(), form.getPassword());
            map.put(DatStatManager.DatStatSettings.Options.metadata.name(), form.getMetadata());
            map.put(DatStatManager.DatStatSettings.Options.enableReload.name(), String.valueOf(form.isEnableReload()));

            if (form.getReloadInterval() > 0)
                map.put(DatStatManager.DatStatSettings.Options.reloadInterval.name(), String.valueOf(form.getReloadInterval()));
            map.put(DatStatManager.DatStatSettings.Options.metadata.name(), form.getMetadata());
            map.put(DatStatManager.DatStatSettings.Options.reloadUser.name(), String.valueOf(getUser().getUserId()));
            if (form.getReloadDate() != null)
                map.put(DatStatManager.DatStatSettings.Options.reloadDate.name(), form.getReloadDate());

            map.save();

            if (form.isEnableReload())
                DatStatMaintenanceTask.addDatStatContainer(getContainer().getId());
            else
                DatStatMaintenanceTask.removeDatStatContainer(getContainer().getId());

            response.put("success", true);
            response.put("returnUrl", PageFlowUtil.urlProvider(StudyUrls.class).getManageStudyURL(getContainer()).getLocalURIString());

            return response;
        }
    }

    @RequiresPermissionClass(AdminPermission.class)
    public class ReloadDatStat extends ApiAction<DatStatManager.DatStatSettings>
    {
        @Override
        public ApiResponse execute(DatStatManager.DatStatSettings form, BindException errors) throws Exception
        {
            ApiSimpleResponse response = new ApiSimpleResponse();
            try
            {
                StudyReloadSource reloadSource = StudyService.get().getStudyReloadSource(DatStatReloadSource.NAME);

                PipelineJob job = StudyService.get().createReloadSourceJob(getContainer(), getUser(), reloadSource, getViewContext().getActionURL());
                PipelineService.get().queueJob(job);

                response.put("success", true);
                response.put("returnUrl", PageFlowUtil.urlProvider(PipelineUrls.class).urlBegin(getContainer()));
            }
            catch (PipelineValidationException e)
            {
                throw new IOException(e);
            }
            return response;
        }
    }
}