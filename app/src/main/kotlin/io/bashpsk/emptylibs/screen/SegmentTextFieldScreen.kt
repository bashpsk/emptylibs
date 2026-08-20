package io.bashpsk.emptylibs.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.bashpsk.emptylibs.jetpackui.textfield.SegmentTextField
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@Composable
fun SegmentTextFieldScreen() {

    var otp4Digit by retain {
        mutableStateOf<PersistentList<Char?>>(List(4) { null }.toPersistentList())
    }

    var otp6Digit by retain {
        mutableStateOf<PersistentList<Char?>>(List(6) { null }.toPersistentList())
    }

    var password6Digit by retain {
        mutableStateOf<PersistentList<Char?>>(List(6) { null }.toPersistentList())
    }

    val isPasswordNotValid by remember {
        derivedStateOf {
            val password = password6Digit.filterNotNull()
            (password.joinToString(separator = "") != "696969").takeIf {
                password.size == password6Digit.size
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 12.dp,
                alignment = Alignment.Top
            )
        ) {

            HorizontalDivider()

            Text("4 Digit - OTP")

            SegmentTextField(
                modifier = Modifier.fillMaxWidth(),
                value = otp4Digit,
                onValueChange = { newValues -> otp4Digit = newValues },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Number
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterVertically
                ),
                itemVerticalAlignment = Alignment.CenterVertically
            )

            HorizontalDivider()

            Text("6 Digit - OTP")

            SegmentTextField(
                modifier = Modifier.fillMaxWidth(),
                value = otp6Digit,
                onValueChange = { newValues -> otp6Digit = newValues },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Number
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterVertically
                ),
                itemVerticalAlignment = Alignment.CenterVertically
            )

            HorizontalDivider()

            Text("6 Digit - PASSWORD")

            SegmentTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password6Digit,
                onValueChange = { newValues -> password6Digit = newValues },
                isError = isPasswordNotValid == true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.NumberPassword
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterVertically
                ),
                itemVerticalAlignment = Alignment.CenterVertically
            )

            when (isPasswordNotValid) {

                true -> Text("Wrong password")
                false -> Text("Correct password")
                null -> {}
            }

            HorizontalDivider()
        }
    }
}