package com.lawyer.project.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import com.lawyer.project.services.EmployeeService;
import com.lawyer.project.services.MessageService;
import com.lawyer.project.services.NotificationService;
import com.lawyer.project.models.Employee;
import com.lawyer.project.models.MailingList;
import com.lawyer.project.models.MassMailBody;
import com.lawyer.project.models.Message;
import com.lawyer.project.repositories.AppointmentRepository;
import com.lawyer.project.repositories.GeneralAnnouncementRepository;
import com.lawyer.project.repositories.MailingListRepository;
import com.lawyer.project.repositories.MassMailBodyRepository;
import com.lawyer.project.repositories.SubscriberListMassMailing;
import com.lawyer.project.repositories.UserCredentialRepository;
import com.lawyer.project.repositories.UserListCredentialRepository;
import com.lawyer.project.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.lawyer.project.UserCredentials;
import com.lawyer.project.dao.impl.MessageDaoImpl;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private GeneralAnnouncementRepository announcementRepo;
    @Autowired
    private UserCredentialRepository userCredentialRepository;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    MessageService messageService;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private UserListCredentialRepository userListCredentialRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private MailingListRepository mailingListRepository;
    @Autowired
    private SubscriberListMassMailing subscriberListMassMailing;
    @Autowired
    private MassMailBodyRepository massMailBodyRepository;


    
    
    
    public HomeController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    //@GetMapping("/")
    // public String showUsers(Model model){
    //     model.addAttribute("users", userRepo.findAll());
    //     return "index";
    // }

    @GetMapping("/index")
    public String showIndecks(){
        return "index";
    }

    @GetMapping("/addUser")
    public String addUser(Model model){
        UserCredentials user = new UserCredentials();
        model.addAttribute("user", user);
        return "addUser";
    }
    
    @GetMapping("/massMail")
    public String massMail(Model model){
        MassMailBody massmail = new MassMailBody();
        model.addAttribute("massmail", massmail);
        return "/massMail";
    }

    @PostMapping("/massMail")
    public String mailAll(@ModelAttribute("massmail") MassMailBody massmail, Model model){
        massMailBodyRepository.addBody(massmail);
        List <MailingList> l = subscriberListMassMailing.getAllMails();
        for(int i=0;i<l.size();i++){
            MailingList mail = l.get(i);
            notificationService.massMail(mail.getEmail(), massmail.getBody());
        }
        return "thanks";
    }

//     @PostMapping("/upload")
//     public ResponseEntity uploadToLocalFileSystem(@RequestParam("file") MultipartFile file) {
//         String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//         Path path = Paths.get(fileBasePath + fileName);
//         try {
//             Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//         String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
//                 .path("/files/download/")
//                 .path(fileName)
//                 .toUriString();
//         return ResponseEntity.ok(fileDownloadUri);
//     }

//     @PostMapping("/multi-upload")
//     public ResponseEntity multiUpload(@RequestParam("files") MultipartFile[] files) {
//         List<Object> fileDownloadUrls = new ArrayList<>();
//         Arrays.asList(files)
//                 .stream()
//                 .forEach(file -> fileDownloadUrls.add(uploadToLocalFileSystem(file).getBody()));
//         return ResponseEntity.ok(fileDownloadUrls);
// }


    @GetMapping("/addMail")
    public String addMail(Model model){
        MailingList mail = new MailingList();
        model.addAttribute("mail", mail);
        //mailingListRepository.addMail("venkatshanmukha793@gmail.com");
        return "addMail";
    }



    @PostMapping("/addEmail")
    public String addMail(@ModelAttribute("mail") MailingList mail, Model model){

        mailingListRepository.addMail(mail.getEmail());
        return "thanks";
    }

    @PostMapping("/addUser")
    public void processAddUser(@ModelAttribute("user") UserCredentials user, Model model){
        //UserCredentials user = new UserCredentials();
        //model.addAttribute("user", user);
        userCredentialRepository.addUser(user.getUsername(), user.getPassword(), " ", user.getEmail(), "p");
        try{
            notificationService.sendNotification(user);
        }catch(MailException e){
            //catch error
        }

        
        System.out.println(user.getUsername());
    }

    @GetMapping("/")
    public String showIndex(Model model){
        //Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //String name = auth.getName();
        //Message m = messageService.getMessagesForUser(name);
        //System.out.println(m.getBody());
        //List <Message> n = messageService.getAllMessages();
        //System.out.println(n.size());
        //userCredentialRepository.addUser("ven793", "1", "address", "email", "phone");
        //System.out.println(name);
        //Date date = new Date();
        //appointmentRepository.addAppointment("venky", "varanasi", "fun", "email", "contact_number", date);
        //appointmentRepository.addAppointment("donky", "varanasi", "fun", "email", "contact_number", date);
        System.out.println(userListCredentialRepository.getAllUsers().size());
        model.addAttribute("announcement", announcementRepo.getAnn());

        //announcementRepo.putAnn();
        return "lawyers/index";
    }

    @RequestMapping(value = "/1", method = RequestMethod.GET)
    public ModelAndView showForm() {
        return new ModelAndView("1", "employee", new Employee());
    }


    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/logout-success")
    public String logoutPage(){
        return "logout";
    }

    @GetMapping("/home")
    public String homepage(){
        return "home";
    }
    
    @GetMapping("/addAppointment")
    public String appointmentPage(){
        return "home";
    }

    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }

    @RequestMapping(value = "/addEmployee", method=RequestMethod.GET)
    public ModelAndView show() {
        return new ModelAndView("1","emp", new Employee());
    }

    // @RequestMapping(value = "/addEmployee", method=RequestMethod.POST)
    // public ModelAndView processRequest(@ModelAttribute("emp") Employee emp) {
    //     model.addAttribute("name", employee.getName());
    //     model.addAttribute("contactNumber", employee.getContactNumber());
    //     model.addAttribute("id", employee.getId());
    // }

    // @RequestMapping(value = "/addNewEmployee", method = RequestMethod.POST)
	// public ModelAndView processRequest(@ModelAttribute("emp") Employee emp) {
	// 	employeeService.insertEmployee(emp);
	// 	List<Employee> employees = employeeService.getAllEmployees();
	// 	ModelAndView model = new ModelAndView("getEmployees");
	// 	model.addObject("employees", employees);
	// 	return model;
	// }

    @RequestMapping(value = "/addEmployee", method = RequestMethod.POST)
	public String processRequest1(@ModelAttribute("emp") Employee emp) {
		employeeService.insertEmployee(emp);
		List<Employee> employees = employeeService.getAllEmployees();
		// ModelAndView model = new ModelAndView("getEmployees");
		// model.addObject("employees", employees);
		return "thanks";
    }
    
    //show all employees saved in db
	@RequestMapping("/getEmployees")
	public ModelAndView getEmployees() {
		List<Employee> employees = employeeService.getAllEmployees();
		ModelAndView model = new ModelAndView("getEmployees");
		model.addObject("employees", employees);
		return model;
	}
}