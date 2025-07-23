package com.example.testcoffe.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class AsteriskPasswordVisualTransformation: VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformed = AnnotatedString("*".repeat(text.text.length))

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = offset
            override fun transformedToOriginal(offset: Int): Int = offset
        }

        return TransformedText(transformed, offsetMapping)
    }
}