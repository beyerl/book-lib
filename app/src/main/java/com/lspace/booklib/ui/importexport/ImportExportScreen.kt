package com.lspace.booklib.ui.importexport

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lspace.booklib.di.AppViewModelProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportExportScreen(
    onBack: () -> Unit,
    viewModel: ImportExportViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val message by viewModel.message.collectAsStateWithLifecycle()

    LaunchedEffect(message) {
        message?.let {
            snackbar.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    val exportCsv = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv"),
    ) { uri: Uri? ->
        if (uri == null) {
            viewModel.reportExport("CSV", false)
        } else {
            scope.launch {
                val content = viewModel.buildCsv()
                val ok = writeText(context, uri, content)
                viewModel.reportExport("CSV", ok)
            }
        }
    }

    val exportBookwyrm = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv"),
    ) { uri: Uri? ->
        if (uri == null) {
            viewModel.reportExport("BookWyrm CSV", false)
        } else {
            scope.launch {
                val content = viewModel.buildBookwyrmCsv()
                val ok = writeText(context, uri, content)
                viewModel.reportExport("BookWyrm CSV", ok)
            }
        }
    }

    val exportMarkdown = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/markdown"),
    ) { uri: Uri? ->
        if (uri == null) {
            viewModel.reportExport("Markdown", false)
        } else {
            scope.launch {
                val content = viewModel.buildMarkdown()
                val ok = writeText(context, uri, content)
                viewModel.reportExport("Markdown", ok)
            }
        }
    }

    val importCsv = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val text = readText(context, uri)
                if (text != null) viewModel.importCsv(text)
                else viewModel.reportExport("Import", false)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import / Export") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SectionCard(
                title = "Export",
                body = "Save your library to a Goodreads- or BookWyrm-compatible CSV, or a Markdown file.",
            ) {
                Button(
                    onClick = { exportCsv.launch("lspace-library.csv") },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Export Goodreads CSV") }
                OutlinedButton(
                    onClick = { exportBookwyrm.launch("lspace-library-bookwyrm.csv") },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Export BookWyrm CSV") }
                OutlinedButton(
                    onClick = { exportMarkdown.launch("lspace-library.md") },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Export Markdown") }
            }

            SectionCard(
                title = "Import",
                body = "Import books from a Goodreads or BookWyrm CSV export. The format is detected automatically and shelves are mapped for you.",
            ) {
                Button(
                    onClick = { importCsv.launch(arrayOf("text/csv", "text/comma-separated-values", "text/plain", "*/*")) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Import CSV") }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    body: String,
    content: @Composable () -> Unit,
) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}

private fun writeText(context: android.content.Context, uri: Uri, text: String): Boolean = try {
    context.contentResolver.openOutputStream(uri)?.use { it.write(text.toByteArray()) }
    true
} catch (e: Exception) {
    false
}

private fun readText(context: android.content.Context, uri: Uri): String? = try {
    context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
} catch (e: Exception) {
    null
}
