package com.example.laboratorio08

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.launch
import com.example.laboratorio08.ui.theme.Laboratorio08Theme
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Laboratorio08Theme {
                val db = Room.databaseBuilder(
                    applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                ).build()

                val taskDao = db.taskDao()
                val viewModel = TaskViewModel(taskDao)

                TaskScreen(viewModel)
            }
        }
    }
}

@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var newTaskDescription by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Sección para agregar una nueva tarea
        AddTaskSection(newTaskDescription) { description ->
            if (description.isNotEmpty()) {
                viewModel.addTask(description)
                newTaskDescription = ""
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sección para la lista de tareas con opciones de edición y eliminación
        TaskList(tasks, viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para eliminar todas las tareas
        Button(
            onClick = { coroutineScope.launch { viewModel.deleteAllTasks() } },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Eliminar todas las tareas")
        }

        // Opciones de filtro, búsqueda y ordenación
        FilterAndSortOptions(viewModel)
    }
}

@Composable
fun AddTaskSection(newTaskDescription: String, onTaskAdded: (String) -> Unit) {
    var description by remember { mutableStateOf(newTaskDescription) }

    Column {
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Nueva tarea") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onTaskAdded(description) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Agregar tarea")
        }
    }
}

@Composable
fun TaskList(tasks: List<Task>, viewModel: TaskViewModel) {
    tasks.forEach { task ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = task.description, modifier = Modifier.weight(1f))
            Button(onClick = { viewModel.toggleTaskCompletion(task) }) {
                Text(if (task.isCompleted) "Completada" else "Pendiente")
            }
            Button(onClick = { viewModel.deleteTask(task) }) {
                Text("Eliminar")
            }
        }
    }
}

@Composable
fun FilterAndSortOptions(viewModel: TaskViewModel) {
    Column {
        // Opciones de filtro
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            Button(onClick = { viewModel.setFilter("all") }) { Text("Todos") }
            Button(onClick = { viewModel.setFilter("completed") }) { Text("Completadas") }
            Button(onClick = { viewModel.setFilter("pending") }) { Text("Pendientes") }
        }

        // Barra de búsqueda
        TextField(
            value = viewModel.searchQuery.collectAsState().value,
            onValueChange = { viewModel.setSearchQuery(it) },
            label = { Text("Buscar tarea") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // Opciones de ordenación
        Row {
            Button(onClick = { viewModel.sortTasks("name") }) { Text("Ordenar por Nombre") }
            Button(onClick = { viewModel.sortTasks("completed") }) { Text("Ordenar por Estado") }
        }
    }
}
@Composable
fun PrioritySelector(priority: String, onPrioritySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        // Botón para abrir el DropdownMenu
        Button(
            onClick = { expanded = !expanded }, // Alterna el estado de expansión del menú
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Prioridad: $priority")
        }

        // DropdownMenu para seleccionar la prioridad
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false } // Cierra el menú cuando se hace clic fuera
        ) {
            listOf("alta", "media", "baja").forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onPrioritySelected(option)
                        expanded = false // Cierra el menú al seleccionar una opción
                    },
                    text = { Text(text = option) }
                )
            }
        }
    }
}


class ReminderWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        // Lógica para mostrar la notificación al usuario
        return Result.success()
    }
}

fun scheduleNotification(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.HOURS).build()
    WorkManager.getInstance(context).enqueue(workRequest)
}

