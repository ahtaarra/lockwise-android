/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package mozilla.lockbox.store

import android.content.SharedPreferences
import io.reactivex.observers.TestObserver
import mozilla.lockbox.DisposingTest
import mozilla.lockbox.action.SettingAction
import mozilla.lockbox.flux.Dispatcher
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class SettingStoreTest : DisposingTest() {
    @Mock
    val sharedPreferences: SharedPreferences = Mockito.mock(SharedPreferences::class.java)
    @Mock
    val editor: SharedPreferences.Editor = Mockito.mock(SharedPreferences.Editor::class.java)

    val dispatcher = Dispatcher()
    val subject = SettingStore(dispatcher)
    val sendUsageDataObserver = TestObserver<Boolean>()

    @Before
    fun setUp() {
        `when`(editor.putBoolean(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(editor)
        `when`(sharedPreferences.edit()).thenReturn(editor)

        subject.apply(sharedPreferences)
        subject.sendUsageData.subscribe(sendUsageDataObserver)
    }

    @Test
    fun sendUsageDataTest_defaultValue() {
        val defaultValue = true
        sendUsageDataObserver.assertValue(defaultValue)
    }

    @Test
    fun sendUsageDataTest_newValue() {
        val newValue = false
        val defaultValue = true
        subject.apply(sharedPreferences)
        subject.sendUsageData.subscribe(sendUsageDataObserver)
        sendUsageDataObserver.assertValue(defaultValue)

        var action = SettingAction.SendUsageData(newValue)
        dispatcher.dispatch(action)

        Mockito.verify(editor).putBoolean(Mockito.anyString(), Mockito.anyBoolean())
        Mockito.verify(editor).apply()
    }
}