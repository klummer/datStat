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

import org.labkey.study.xml.datStatExport.DatStatConfigDocument;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by klum on 3/14/15.
 */
public class DatStatProject
{
    private String _name;
    private Map<String, Form> _formMap = new HashMap<>();

    public static String DEFAULT_DATE_FIELD = "date";
    public static String DEFAULT_PTID_FIELD = "DATSTAT_ALTPID";


    public DatStatProject(DatStatConfigDocument.DatStatConfig.Projects.Project config)
    {
        _name = config.getProjectName();

        parseProjectConfig(config);
    }

    public String getName()
    {
        return _name;
    }

    private void parseProjectConfig(DatStatConfigDocument.DatStatConfig.Projects.Project config)
    {
        for (DatStatConfigDocument.DatStatConfig.Projects.Project.Forms.Form xmlForm : config.getForms().getFormArray())
        {
            Form form = new Form(xmlForm.getFormName());

            form.setDemographic(xmlForm.getDemographic());
            form.setTransform(xmlForm.getTransform());

            if (xmlForm.isSetDateField())
                form.setDateField(xmlForm.getDateField());
            if (xmlForm.isSetPtidField())
                form.setPtidField(xmlForm.getPtidField());

            _formMap.put(xmlForm.getFormName(), form);
        }
    }

    public Map<String, Form> getFormMap()
    {
        return Collections.unmodifiableMap(_formMap);
    }

    public static class Form
    {
        private String _name;
        private boolean _demographic;
        private boolean _transform;
        private String _dateField = DEFAULT_DATE_FIELD;
        private String _ptidField = DEFAULT_PTID_FIELD;

        public Form(String name)
        {
            _name = name;
        }

        public String getName()
        {
            return _name;
        }

        public boolean isDemographic()
        {
            return _demographic;
        }

        public void setDemographic(boolean demographic)
        {
            _demographic = demographic;
        }

        public boolean isTransform()
        {
            return _transform;
        }

        public void setTransform(boolean transform)
        {
            _transform = transform;
        }

        public String getDateField()
        {
            return _dateField;
        }

        public void setDateField(String dateField)
        {
            _dateField = dateField;
        }

        public String getPtidField()
        {
            return _ptidField;
        }

        public void setPtidField(String ptidField)
        {
            _ptidField = ptidField;
        }
    }
}
