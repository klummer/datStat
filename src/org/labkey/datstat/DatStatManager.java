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

import org.labkey.api.data.PropertyManager;
import org.labkey.api.writer.ContainerUser;

import java.util.Map;

public class DatStatManager
{
    private static final DatStatManager _instance = new DatStatManager();
    public static final String DATSTAT_PROPERTIES = "DatstatConfigurationSettings";

    private DatStatManager()
    {
        // prevent external construction with a private default constructor
    }

    public static DatStatManager get()
    {
        return _instance;
    }

    public DatStatSettings getDatStatSettings(ContainerUser containerUser)
    {
        DatStatSettings form = new DatStatSettings();
        Map<String, String> map = PropertyManager.getEncryptedStore().getProperties(containerUser.getContainer(), DATSTAT_PROPERTIES);

        if (map.containsKey(DatStatManager.DatStatSettings.Options.url.name()))
            form.setBaseServerUrl(map.get(DatStatManager.DatStatSettings.Options.url.name()));
        if (map.containsKey(DatStatManager.DatStatSettings.Options.user.name()))
            form.setUsername(map.get(DatStatManager.DatStatSettings.Options.user.name()));
        if (map.containsKey(DatStatManager.DatStatSettings.Options.password.name()))
            form.setPassword(map.get(DatStatManager.DatStatSettings.Options.password.name()));
        if (map.containsKey(DatStatManager.DatStatSettings.Options.metadata.name()))
            form.setMetadata(map.get(DatStatManager.DatStatSettings.Options.metadata.name()));
        if (map.containsKey(DatStatManager.DatStatSettings.Options.enableReload.name()))
            form.setEnableReload(Boolean.parseBoolean(map.get(DatStatManager.DatStatSettings.Options.enableReload.name())));
        if (map.containsKey(DatStatManager.DatStatSettings.Options.reloadInterval.name()))
            form.setReloadInterval(Integer.parseInt(map.get(DatStatManager.DatStatSettings.Options.reloadInterval.name())));
        if (map.containsKey(DatStatManager.DatStatSettings.Options.reloadDate.name()))
            form.setReloadDate(map.get(DatStatManager.DatStatSettings.Options.reloadDate.name()));

        return form;
    }

    /**
     * Represents the serialized redcap settings roundtripped through the configuration UI
     */
    public static class DatStatSettings
    {
        public enum Options
        {
            url,
            user,
            password,
            reloadInterval,
            enableReload,
            reloadDate,
            metadata,
            reloadUser,
        }

        private String _baseServerUrl;
        private String _username;
        private String _password;
        private String _metadata;
        private boolean _unzipContents = true;
        private boolean _enableReload;
        private String _reloadDate;
        private int _reloadUser;
        private int _reloadInterval;

        public String getBaseServerUrl()
        {
            return _baseServerUrl;
        }

        public void setBaseServerUrl(String baseServerUrl)
        {
            _baseServerUrl = baseServerUrl;
        }

        public String getUsername()
        {
            return _username;
        }

        public void setUsername(String username)
        {
            _username = username;
        }

        public String getPassword()
        {
            return _password;
        }

        public void setPassword(String password)
        {
            _password = password;
        }

        public String getMetadata()
        {
            return _metadata;
        }

        public void setMetadata(String metadata)
        {
            _metadata = metadata;
        }

        public boolean isUnzipContents()
        {
            return _unzipContents;
        }

        public void setUnzipContents(boolean unzipContents)
        {
            _unzipContents = unzipContents;
        }

        public boolean isEnableReload()
        {
            return _enableReload;
        }

        public void setEnableReload(boolean enableReload)
        {
            _enableReload = enableReload;
        }

        public String getReloadDate()
        {
            return _reloadDate;
        }

        public void setReloadDate(String reloadDate)
        {
            _reloadDate = reloadDate;
        }

        public int getReloadUser()
        {
            return _reloadUser;
        }

        public void setReloadUser(int reloadUser)
        {
            _reloadUser = reloadUser;
        }

        public int getReloadInterval()
        {
            return _reloadInterval;
        }

        public void setReloadInterval(int reloadInterval)
        {
            _reloadInterval = reloadInterval;
        }
    }
}