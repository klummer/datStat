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

import org.labkey.api.data.DbSchema;
import org.labkey.api.data.dialect.SqlDialect;

public class DatStatSchema
{
    private static final DatStatSchema _instance = new DatStatSchema();
    public static final String NAME = "datstat";

    public static DatStatSchema getInstance()
    {
        return _instance;
    }

    private DatStatSchema()
    {
        // private constructor to prevent instantiation from
        // outside this class: this singleton should only be
        // accessed via org.labkey.datstat.DatStatSchema.getInstance()
    }

    public DbSchema getSchema()
    {
        return DbSchema.get(NAME);
    }

    public SqlDialect getSqlDialect()
    {
        return getSchema().getSqlDialect();
    }
}
