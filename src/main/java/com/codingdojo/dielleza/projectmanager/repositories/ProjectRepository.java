package com.codingdojo.dielleza.projectmanager.repositories;

import java.util.List;

import com.codingdojo.dielleza.projectmanager.models.Project;
import com.codingdojo.dielleza.projectmanager.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {
    List<Project> findAll();
    Project findByIdIs(Long id);
    List<Project> findAllByUsers(User user);
    List<Project> findByUsersNotContains(User user);
}