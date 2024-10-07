package com.example.laboratorio08

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val dao: TaskDao) : ViewModel() {
    // Estado para la lista de tareas
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        // Al inicializar, cargamos las tareas de la base de datos
        viewModelScope.launch {
            _tasks.value = dao.getAllTasks()
        }
    }

    // Función para añadir una nueva tarea
    fun addTask(description: String) {
        val newTask = Task(description = description)
        viewModelScope.launch {
            dao.insertTask(newTask)
            _tasks.value = dao.getAllTasks() // Recargamos la lista
        }
    }

    // Función para alternar el estado de completado de una tarea
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            dao.updateTask(updatedTask)
            _tasks.value = dao.getAllTasks() // Recargamos la lista
        }
    }

    // Función para eliminar todas las tareas
    fun deleteAllTasks() {
        viewModelScope.launch {
            dao.deleteAllTasks()
            _tasks.value = emptyList() // Vaciamos la lista en el estado
        }
    }
    fun editTask(task: Task, newDescription: String) {
        val updatedTask = task.copy(description = newDescription)
        viewModelScope.launch {
            dao.updateTask(updatedTask)
            _tasks.value = dao.getAllTasks() // Recargamos la lista
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task) // Asegúrate de tener este método en TaskDao
            _tasks.value = dao.getAllTasks() // Recargamos la lista
        }
    }
    private val _filter = MutableStateFlow("all") // "all", "completed", "pending"
    val filter: StateFlow<String> = _filter

    fun setFilter(newFilter: String) {
        _filter.value = newFilter
    }

    fun getFilteredTasks(): List<Task> {
        return when (_filter.value) {
            "completed" -> _tasks.value.filter { it.isCompleted }
            "pending" -> _tasks.value.filter { !it.isCompleted }
            else -> _tasks.value
        }
    }
    private var _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    fun sortTasks(criteria: String) {
        _tasks.value = when (criteria) {
            "name" -> _tasks.value.sortedBy { it.description }
            "completed" -> _tasks.value.sortedBy { it.isCompleted }
            // Puedes agregar otros criterios aquí
            else -> _tasks.value
        }
    }

}
