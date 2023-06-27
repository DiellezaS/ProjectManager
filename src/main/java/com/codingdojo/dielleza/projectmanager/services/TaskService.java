package com.codingdojo.dielleza.projectmanager.services;

import com.codingdojo.dielleza.projectmanager.models.Task;
import com.codingdojo.dielleza.projectmanager.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepo;



    public List<Task> allTasks(){
        return taskRepo.findAll();
    }

    public List<Task> projectTasks(Long projectId){
        return taskRepo.findByProjectIdIs(projectId);
    }

    public Task addTask(Task task) {
        return taskRepo.save(task);
    }

    public void deleteTask(Task task) {
        taskRepo.delete(task);
    }
}