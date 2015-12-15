/*
 * Copyright (c) 2010 - 2015 Ushahidi Inc
 * All rights reserved
 * Contact: team@ushahidi.com
 * Website: http://www.ushahidi.com
 * GNU Lesser General Public License Usage
 * This file may be used under the terms of the GNU Lesser
 * General Public License version 3 as published by the Free Software
 * Foundation and appearing in the file LICENSE.LGPL included in the
 * packaging of this file. Please review the following information to
 * ensure the GNU Lesser General Public License version 3 requirements
 * will be met: http://www.gnu.org/licenses/lgpl.html.
 *
 * If you have questions regarding the use of this file, please contact
 * Ushahidi developers at team@ushahidi.com.
 */

package org.addhen.smssync.data.message;

import com.addhen.android.raiburari.data.pref.BooleanPreference;

import org.addhen.smssync.data.PrefsFactory;
import org.addhen.smssync.data.cache.FileManager;
import org.addhen.smssync.data.entity.Message;
import org.addhen.smssync.data.entity.SyncUrl;
import org.addhen.smssync.data.net.MessageHttpClient;
import org.addhen.smssync.data.repository.datasource.filter.FilterDataSource;
import org.addhen.smssync.data.repository.datasource.message.MessageDataSource;
import org.addhen.smssync.data.repository.datasource.webservice.WebServiceDataSource;
import org.addhen.smssync.smslib.sms.ProcessSms;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test PostMessage
 *
 * @author Ushahidi Team <team@ushahidi.com>
 */
public class PostMessageTest {

    private PostMessage mPostMessage;

    @Mock
    private Context mMockContext;

    @Mock
    private MessageHttpClient mMockMessageHttpClient;

    @Mock
    private MessageDataSource mMockMessageDataSource;

    private WebServiceDataSource mMockWebServiceDataSource;

    @Mock
    private FilterDataSource mMockFilterDataSource;

    @Mock
    private ProcessSms mMockProcessSms;

    @Mock
    private FileManager mMockFileManager;

    @Mock
    private ProcessMessageResult mMockProcessMessageResult;

    @Mock
    private PrefsFactory mMockPrefsFactory;

    @Mock
    private Message mMockMessage;


    @Mock
    private SyncUrl mMockSyncUrl;

    @Mock
    private SharedPreferences mSharedPreferences;

    @Mock
    BooleanPreference mBooleanPreference;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mPostMessage = new PostMessage(mMockContext, mMockPrefsFactory, mMockMessageHttpClient,
                mMockMessageDataSource, mMockWebServiceDataSource, mMockFilterDataSource,
                mMockProcessSms, mMockFileManager, mMockProcessMessageResult);
    }

    @Test
    public void successfullyRouteSMSToSingleWebService() throws IOException {
        List<SyncUrl> syncUrls = Arrays.asList(mMockSyncUrl);

        final String fromField = "0000000";

        final String deviceId = "01234567";
        given(mMockPrefsFactory.serviceEnabled()).willReturn(mBooleanPreference);
        given(mBooleanPreference.get()).willReturn(true);
        // Enable SMSsync service
        given(mMockPrefsFactory.serviceEnabled().get()).willReturn(true);

        given(mMockPrefsFactory.enableReply()).willReturn(mBooleanPreference);
        given(mBooleanPreference.get()).willReturn(false);
        // Disable AutoReply
        given(mMockPrefsFactory.enableReply().get()).willReturn(false);

//        given(mMockWebServiceDataSource.get(SyncUrl.Status.ENABLED)).willReturn(syncUrls);
        given(mMockPrefsFactory.enableWhitelist()).willReturn(mBooleanPreference);
        given(mBooleanPreference.get()).willReturn(false);
        // Don't process whitelist
        given(mMockPrefsFactory.enableWhitelist().get()).willReturn(false);

        given(mMockPrefsFactory.enableBlacklist()).willReturn(mBooleanPreference);
        given(mBooleanPreference.get()).willReturn(false);

        // Don't process blacklist
        given(mMockPrefsFactory.enableBlacklist().get()).willReturn(false);

        given(mMockMessage.getMessageFrom()).willReturn(fromField);

        given(mMockPrefsFactory.uniqueId().get()).willReturn(deviceId);

        mPostMessage.routePendingMessage(mMockMessage);

        verify(mMockMessageHttpClient, times(1))
                .postSmsToWebService(syncUrls.get(0), mMockMessage, fromField, deviceId);
    }
}
