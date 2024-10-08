package com.codingdojo.dielleza.projectmanager.services;

import com.codingdojo.dielleza.projectmanager.models.Project;
import com.codingdojo.dielleza.projectmanager.models.User;
import com.codingdojo.dielleza.projectmanager.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;



    public List<Project> allProjects(){
        return projectRepository.findAll();
    }

    public Project updateProject(Project project) {
        return projectRepository.save(project);
    }

    public List<Project> getAssignedProjects(User user){
        return projectRepository.findAllByUsers(user);
    }

    public List<Project> getUnassignedProjects(User user){
        return projectRepository.findByUsersNotContains(user);
    }

    public Project addProject(Project project) {
        return projectRepository.save(project);
    }

    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }

    public Project findById(Long id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if(optionalProject.isPresent()) {
            return optionalProject.get();
        }else {
            return null;
        }
    }

}