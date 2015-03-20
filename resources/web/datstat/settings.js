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

Ext4.define('LABKEY.ext4.DatStatSettings', {

    extend: 'Ext.tab.Panel',

    constructor: function (config)
    {
        Ext4.applyIf(config, {
            bodyPadding : 20,
            buttonAlign : 'left',
            bodyStyle   : 'background-color: transparent;',
            defaults : {
                height: 400,
                border: false,
                frame: false,
                cls: 'iScroll', // webkit custom scroll bars
                bodyStyle: 'background-color: transparent;'
            }
        });

        this.currentRow = 0;
        this.callParent([config]);
    },

    initComponent: function ()
    {
        var items = [{
            xtype       : 'displayfield',
            value       : '<span>This configuration enables the automatic reloading of study data directly from a DATStat server. The credentials specified ' +
            'will be used to connect to the remote DATStat server.</span><p>'
        },{
            xtype       : 'textfield',
            fieldLabel  : 'DATStat Server Base URL',
            allowBlank  : false,
            name        : 'baseServerUrl',
            emptyText   : 'http://10.10.10.54/api',
            value       : this.bean.baseServerUrl
        },{
            xtype       : 'textfield',
            fieldLabel  : 'User Name',
            allowBlank  : false,
            name        : 'username',
            value       : this.bean.username
        }, {
            xtype       : 'textfield',
            fieldLabel  : 'Password',
            allowBlank  : false,
            name        : 'password',
            inputType   : 'password',
            value       : this.bean.password
        },{
            xtype       : 'checkbox',
            fieldLabel  : 'Enable Reloading',
            name        : 'enableReload',
            checked     : this.bean.enableReload,
            uncheckedValue : 'false',
            listeners : {
                scope: this,
                'change': function (cb, value)
                {
                    cb.up('panel').down('numberfield[name=reloadInterval]').setDisabled(!value);
                    cb.up('panel').down('datefield[name=reloadDate]').setDisabled(!value);
                }
            }
        },{
            xtype       : 'datefield',
            fieldLabel  : 'Load on',
            disabled    : !this.bean.enableReload,
            allowBlank  : false,
            name        : 'reloadDate',
            format      : 'Y-m-d',
            altFormats  : '',
            value       : this.bean.reloadDate
        },{
            xtype       : 'numberfield',
            fieldLabel  : 'Repeat (days)',
            disabled    : !this.bean.enableReload,
            name        : 'reloadInterval',
            value       : this.bean.reloadInterval,
            minValue    : 1
        }];

        var formPanel = {
            xtype   : 'form',
            title   : 'Connection',
            trackResetOnLoad : true,
            autoScroll : true,
            fieldDefaults  : {
                labelWidth : 200,
                width : 450,
                height : 22,
                labelSeparator : ''
            },
            items : items
        };

        var metadata = {
            xtype   : 'form',
            title   : 'Configuration Setting',
            trackResetOnLoad : true,
            fieldDefaults  : {
                labelWidth : 200,
                width : 550,
                height : 22,
                labelSeparator : ''
            },
            items : [{
                xtype       : 'displayfield',
                value       : '<span>Add XML metadata to configure which DATStat projects are mapped to LabKey studies. ' +
                'Click on this ' + this.helpLink + ' for documentation on XML schema.</span><p>'
            },{
                xtype       : 'textarea',
                name        : 'metadata',
                height      : 300,
                value       : this.bean.metadata
            }]
        };

        this.items = [formPanel, metadata];

        this.buttons = [{
            text    : 'Save',
            handler : function(btn) {
                var form = btn.up('tabpanel').items.getAt(0).getForm();
                var formAdvanced = btn.up('tabpanel').items.getAt(1).getForm();
                if (form.isValid() && formAdvanced.isValid())
                {
                    this.getEl().mask("Saving...");
                    var params = form.getValues();

                    Ext4.apply(params, formAdvanced.getValues());
                    Ext4.Ajax.request({
                        url    : LABKEY.ActionURL.buildURL('datstat', 'saveDatStatConfig.api'),
                        method  : 'POST',
                        params  : params,
                        submitEmptyText : false,
                        success : function(response){
                            var o = Ext4.decode(response.responseText);
                            this.getEl().unmask();

                            if (o.success) {
                                var msgbox = Ext4.create('Ext.window.Window', {
                                    title    : 'Save Complete',
                                    modal    : false,
                                    closable : false,
                                    border   : false,
                                    html     : '<div style="padding: 15px;"><span class="labkey-message">' + 'DATStat configuration saved successfully' + '</span></div>'
                                });
                                msgbox.show();
                                msgbox.getEl().fadeOut({duration : 3000, callback : function(){ msgbox.close(); }});

                                form.setValues(form.getValues());
                                form.reset();

                                formAdvanced.setValues(formAdvanced.getValues());
                                formAdvanced.reset();
                            }
                        },
                        failure : function(response){
                            this.getEl().unmask();
                            Ext4.Msg.alert('Failure', Ext4.decode(response.responseText).exception);
                        },
                        scope : this
                    });
                }
            },
            scope   : this
        },{
            text    : 'Reload Now',
            handler : function(btn) {
                var form = btn.up('tabpanel').items.getAt(0).getForm();
                var formAdvanced = btn.up('tabpanel').items.getAt(1).getForm();
                if (!form.isDirty() && !formAdvanced.isDirty())
                {
                    this.getEl().mask("Reloading DATStat...");
                    var params = form.getValues();

                    Ext4.apply(params, formAdvanced.getValues());
                    Ext4.Ajax.request({
                        url    : LABKEY.ActionURL.buildURL('datstat', 'reloadDatStat.api'),
                        method  : 'POST',
                        params  : params,
                        submitEmptyText : false,
                        success : function(response){
                            var o = Ext4.decode(response.responseText);
                            this.getEl().unmask();

                            if (o.success)
                                window.location = o.returnUrl;
                        },
                        failure : function(response){
                            this.getEl().unmask();
                            Ext4.Msg.alert('Failure', Ext4.decode(response.responseText).exception);
                        },
                        scope : this
                    });
                }
                else
                    Ext4.Msg.alert('Failure', 'There are unsaved changes in your settings. Please save before starting the reload.');
            },
            scope   : this
        }];

        this.callParent();
    }
});
