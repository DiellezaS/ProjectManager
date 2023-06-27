package com.codingdojo.dielleza.projectmanager.controllers;

import com.codingdojo.dielleza.projectmanager.models.LoginUser;
import com.codingdojo.dielleza.projectmanager.models.Project;
import com.codingdojo.dielleza.projectmanager.models.Task;
import com.codingdojo.dielleza.projectmanager.models.User;
import com.codingdojo.dielleza.projectmanager.services.ProjectService;
import com.codingdojo.dielleza.projectmanager.services.TaskService;
import com.codingdojo.dielleza.projectmanager.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TaskService taskService;

    @GetMapping("/")
    public String index(Model model, @ModelAttribute("newUser") User newUser,
                        @ModelAttribute("newLogin") User newLogin, HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";}

        model.addAttribute("newUser", new User());
        model.addAttribute("newLogin", new LoginUser());

        return "index";
    }


    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("newUser") User newUser,
                           BindingResult result, Model model, HttpSession session) {

        User user = userService.register(newUser, result);

        if(result.hasErrors()) {
            model.addAttribute("newLogin", new LoginUser());
            return "index";
        }
        session.setAttribute("userId", user.getId());

        return "redirect:/dashboard";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("newLogin") LoginUser newLogin,
                        BindingResult result, Model model, HttpSession session) {

        User user = userService.login(newLogin, result);

        if(result.hasErrors() || user==null) {
            model.addAttribute("newUser", new User());
            return "index";
        }

        session.setAttribute("userId", user.getId());

        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        if(session.getAttribute("userId") == null) {
            return "redirect:/logout";
        }
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);

        model.addAttribute("user", user);
        model.addAttribute("unassignedProjects", projectService.getUnassignedProjects(user));
        model.addAttribute("assignedProjects", projectService.getAssignedProjects(user));

        return "dashboard";
    }

    @RequestMapping("/dashboard/join/{id}")
    public String joinTeam(@PathVariable("id") Long id, HttpSession session, Model model) {

        if(session.getAttribute("userId") == null) {
            return "redirect:/logout";
        }
        Long userId = (Long) session.getAttribute("userId");

        Project project = projectService.findById(id);
        User user = userService.findById(userId);

        user.getProjects().add(project);
        userService.updateUser(user);

        model.addAttribute("user", user);
        model.addAttribute("unassignedProjects", projectService.getUnassignedProjects(user));
        model.addAttribute("assignedProjects", projectService.getAssignedProjects(user));

        return "redirect:/dashboard";
    }

    @RequestMapping("/dashboard/leave/{id}")
    public String leaveTeam(@PathVariable("id") Long id, HttpSession session, Model model) {

        if(session.getAttribute("userId") == null) {
            return "redirect:/logout";
        }
        Long userId = (Long) session.getAttribute("userId");

        Project project = projectService.findById(id);
        User user = userService.findById(userId);

        user.getProjects().remove(project);
        userService.updateUser(user);

        model.addAttribute("user", user);
        model.addAttribute("unassignedProjects", projectService.getUnassignedProjects(user));
        model.addAttribute("assignedProjects", projectService.getAssignedProjects(user));

        return "redirect:/dashboard";
    }

    @GetMapping("/projects/{id}")
    public String viewProject(@PathVariable("id") Long id, HttpSession session, Model model) {

        if(session.getAttribute("userId") == null) {
            return "redirect:/logout";
        }

        Project project = projectService.findById(id);
        model.addAttribute("project", project);
        return "viewProject";
    }

    @GetMapping("/projects/edit/{id}")
    public String openEditProject(@PathVariable("id") Long id, HttpSession session, Model model) {

        if(session.getAttribute("userId") == null) {
            return "redirect:/logout";
        }

        Project project = projectService.findById(id);
        model.addAttribute("project", project);
        return "editProject";
    }

    @PostMapping("/projects/edit/{id}")
    public String editProject(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("project") Project project,
            BindingResult result,
            HttpSession session) {

        if(session.getAttribute("userId") == null) {
            return "redirect:/logout";
        }
        Long userId = (Long) session.getAttribute("userId");

        User user = userService.findById(userId);

        if(result.hasErrors()) {
            return "editProject";
        }else {
            Project thisProject = projectService.findById(id);
            project.setLead(thisProject.getLead());
            project.setLead(user);
            projectService.updateProject(project);
            return "redirect:/dashboard";
        }
    }

    @RequestMapping("/projects/delete/{id}")
    public String deleteProject(@PathVariable("id") Long id, HttpSession session) {

        if(session.getAttribute("userId") == null) {
            return "redirect:/logout";
        }

        Project project = projectService.findById(id);
        projectService.deleteProject(project);

        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.setAttribute("userId", null);
        return "redirect:/";
    }

    @GetMapping("/projects/new")
    public String newProject(@ModelAttribute("project") Project project, HttpSession session, Model model) {
        if(session.getAttribute("userId") == null) {
            return "redirect:/logout";
        }
        Long userId = (Long) session.getAttribute("userId");

        model.addAttribute("userId", userId);
        return "newProject";
    }

    @PostMapping("/projects/new")
    public String addNewProject(@Valid @ModelAttribute("project") Project project, BindingResult result, HttpSession session) {

        if(session.getAttribute("userId") == null) {
            return "redirect:/logout";
        }

        if(result.hasErrors()) {
            return "newProject";
        }else {
            projectService.addProject(project);

            Long userId = (Long) session.getAttribute("userId");
            User user = userService.findById(userId);
            user.getProjects().add(project);
            userService.updateUser(user);
            return "redirect:/dashboard";
        }


    }

    @GetMapping("/projects/{id}/tasks")
    public String viewProjectTasks(@PathVariable("id") Long id, HttpSession session, Model model, @ModelAttribute("task") Task task) {

        if(session.getAttribute("userId") == null) {
            return "redirect:/logout";
        }

        Project project = projectService.findById(id);
        model.addAttribute("project", project);
        model.addAttribute("tasks", taskService.projectTasks(id));
        return "tasks";
    }

    @PostMapping("/projects/{id}/tasks")
    public String newProjectTask(
            @PathVariable("id") Long id,
            HttpSession session,
            Model model,
            @Valid @ModelAttribute("task") Task task,
            BindingResult result) {

        if(session.getAttribute("userId") == null) {
            return "redirect:/logout";
        }
        Long userId = (Long) session.getAttribute("userId");

        Project project = projectService.findById(id);

        if(result.hasErrors()) {
            model.addAttribute("project", project);
            model.addAttribute("tasks", taskService.projectTasks(id));
            return "tasks";
        }else {
            Task newTask = new Task(task.getName());
            newTask.setProject(project);
            newTask.setCreator(userService.findById(userId));
            taskService.addTask(newTask);
            return "redirect:/projects/" + id + "/tasks";
        }
    }

}