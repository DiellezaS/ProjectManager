package com.codingdojo.dielleza.projectmanager.repositories;


import java.util.List;


import com.codingdojo.dielleza.projectmanager.models.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findAll();
    List<Task> findByProjectIdIs(Long id);
}