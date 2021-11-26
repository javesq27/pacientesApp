package com.ub.practicas.lab.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Controller
public class AppController implements WebMvcConfigurer {
    @Autowired
    SendMailService sendMailService;

    @Autowired
    ParticularTaskRepository particularTaskRepo;

    @Autowired
    RutineTaskRepository rutineTaskRepo;

    @Autowired
    PatientRepository patientRepo;

    @Autowired
    ProfessionalRepository professionalRepo;

    @Autowired
    UserRepository userRepo;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    HttpSession session;

    @GetMapping("/")
    public String MainPage() {
        return "MainPageExample";
    }

    @GetMapping("/Register")
    public String ProfessionalRegister(Model model, Professional professional, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("error", Boolean.valueOf(error));
        return "ProfesionalRegister";
    }

    @PostMapping("/RegisteredProfessional")
    public String ProfessionalRegistered(Model model, Professional professional) {
        if (professionalRepo.existsByIdNumber(professional.getIdNumber())
                || userRepo.existsByUsername(professional.getUsername().trim())) {
            return "redirect:/Register?error=true";
        }
        professional.encryptPassword(encoder);
        professionalRepo.save(professional);
        return "redirect:/";
    }

    @GetMapping("/OurProfessionals")
    public String ListOfProfessionals(Model model, @RequestParam(value = "search", required = false) String search) {
        if (search != null){
            model.addAttribute("list", professionalRepo.findByNameLike("%" + search + "%"));
            return "ProfessionalList";
        }
        model.addAttribute("list", professionalRepo.findAll());
        return "ProfessionalList";
    }

    @PostMapping("/OurProfessionals/search")
    public String ListOfProfessionalsFilterProcess(Model model, @RequestParam String search) {
        if (search != null) {
            return "redirect:/OurProfessionals?search=" + search;
        }
        return "redirect:/OurProfessionals";
    }

    @GetMapping("/AboutUs")
    public String aboutUs() {
        return "AboutUs";
    }

    @GetMapping("/login")
    public String login(Model model, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("error", Boolean.valueOf(error));
        return "login";
    }

    @GetMapping("/userProcess")
    public String mainAuthenticated(HttpServletRequest request) {
        session = request.getSession();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        session.setAttribute("userId", userRepo.findByUsername(auth.getName()).get().getId());
        if (request.isUserInRole("ROLE_PROFESSIONAL")) {
            return "redirect:/Professional";
        } else if (request.isUserInRole("ROLE_PATIENT")) {
            loadRutines();
            return "redirect:/Patient";
        }
        return "redirect:/";
    }

    @GetMapping("/Patient")
    public String PatientMain(Model model, Long Id) {
        Id = (Long) session.getAttribute("userId");
        model.addAttribute("list", particularTaskRepo.findByPatientIdOrderByDateDayAscTaskTimeAsc(Id));

        model.addAttribute("today", LocalDate.now());
        model.addAttribute("now", LocalTime.now());

        return "PatientMain";
    }

    @GetMapping("/Patient/newTask")
    public String addTask(Model model, ParticularTask pTask, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("error", Boolean.valueOf(error));
        return "TaskCreation";
    }

    @PostMapping("/Patient/taskCreated")
    public String addingTask(ParticularTask pTask) {
        if (LocalDate.now().isAfter(pTask.getDateDay()) || (LocalTime.now().isAfter(pTask.getTaskTime()) && LocalDate.now().isEqual(pTask.getDateDay()))){
            
            return "redirect:/Patient/newTask?error=true";
        }

        Long idPatient = (Long) session.getAttribute("userId");
        pTask.setPatientId(idPatient);
        particularTaskRepo.save(pTask);
        return "redirect:/Patient";
    }

    @GetMapping("/Patient/Delete/{idTask}")
    public String DeleteTask(@PathVariable(name = "idTask") Long idTask) {
        Long idPatient = (Long) session.getAttribute("userId");
        if (particularTaskRepo.findById(idTask).get().getPatientId() != idPatient) {
            return "redirect:/Patient";
        }

        particularTaskRepo.deleteById(idTask);
        return "redirect:/Patient";
    }

    @GetMapping("/Patient/TaskCompleted/{idTask}")
    public String taskCompleted(@PathVariable(name = "idTask") Long idTask) {
        Long idP = (Long) session.getAttribute("userId");
        if (particularTaskRepo.findById(idTask).get().getPatientId() != idP) {
            return "redirect:/Patient";
        }

        ParticularTask temp_task = particularTaskRepo.findById(idTask).get();
        temp_task.setChecked(!temp_task.isChecked());
        particularTaskRepo.save(temp_task);
        return "redirect:/Patient";
    }

    @GetMapping("/Patient/DeleteCompletedTasks")
    @Transactional
    public String deleteCompletedTasks(Long idPatient) {
        idPatient = (Long) session.getAttribute("userId");
        particularTaskRepo.deleteByCheckedAndPatientId(true, idPatient);
        return "redirect:/Patient";
    }

    @GetMapping("/Patient/newRutine")
    public String addRutine(Model model, RutineTask rTask) {
        model.addAttribute("week", Week.values());
        return "RutineTaskCreation";
    }

    @PostMapping("/Patient/RutineSave")
    public String saveRutine(RutineTask rTask, @RequestParam List<String> dayList){
        rTask.setWeekDays(dayList);
        rTask.setPatientId((Long) session.getAttribute("userId"));
        if (rTask.getWeekDays().contains(LocalDate.now().getDayOfWeek().name()) && rTask.getTaskTime().isAfter(LocalTime.now())){
            particularTaskRepo.save(ParticularTask.addRutine(rTask));
            rutineTaskRepo.save(rTask.updateToday());
        }
        rutineTaskRepo.save(rTask);
        return "redirect:/Patient/RutineList";
    }

    @GetMapping("/Patient/RutineList")
    public String rutineList(Model model){
        model.addAttribute("list", rutineTaskRepo.findByPatientId((Long) session.getAttribute("userId")));
        return "RutineList";
    }

    @GetMapping("/Patient/RutineDelete/{idRutine}")
    public String deleteRutine(@PathVariable(name = "idRutine") Long idRutine){
        Long idP = (Long) session.getAttribute("userId");
        if (rutineTaskRepo.findById(idRutine).get().getPatientId() != idP) {
            return "redirect:/Patient/RutineList";
        }
        rutineTaskRepo.deleteById(idRutine);
        return "redirect:/Patient/RutineList";
    }

    @GetMapping("/Patient/SwitchRepeat/{idRutine}")
    public String switchRepeat(@PathVariable(name = "idRutine") Long idRutine){
        Long idP = (Long) session.getAttribute("userId");
        if (rutineTaskRepo.findById(idRutine).get().getPatientId() != idP) {
            return "redirect:/Patient/RutineList";
        }
        rutineTaskRepo.save(rutineTaskRepo.findById(idRutine).get().switchRepeat());
        return "redirect:/Patient/RutineList";
    }

    @GetMapping("/Patient/Emergency")
    @Transactional
    public String emergency() {
        Patient temp = patientRepo.findById((Long) session.getAttribute("userId")).get();
        temp.setEmergency(true);
        patientRepo.save(temp);

        Long proId = temp.getProId();
        String professionalMail = professionalRepo.findById(proId).get().getUsername();
        String patientName = temp.getName();
        String patientPhone = temp.getPhone();
        sendMailService.sendEmergencyEmail(professionalMail, "El paciente: " + patientName
                + " ha habilitado el modo EMERGENCIA. Por favor contactarse con el mismo lo antes posible al siguiente numero: "
                + patientPhone + " .");

        return "redirect:/Patient";
    }

    @GetMapping("/Professional")
    public String ProfessionalMain(Model model, Long id) {
        id = (Long) session.getAttribute("userId");
        model.addAttribute("list", patientRepo.findByProId(id));
        return "ProfessionalMain";
    }

    @GetMapping("/Professional/addPatient")
    public String addPatient(Model model, Patient patient, @RequestParam(value = "error", required = false) String error){
        model.addAttribute("error", Boolean.valueOf(error));
        return "PatientCreation";
    }

    @PostMapping("/Professional/patientCreated")
    public String addingPatient(Patient patient, User user, Long proId){
        if (userRepo.existsByUsername(patient.getUsername().trim())) {
            return "redirect:/Professional/addPatient?error=true";
        }
        proId = (Long) session.getAttribute("userId");
        patient.setProId(proId);
        patient.encryptPassword(encoder);
        patientRepo.save(patient);
        return "redirect:/Professional";
    }

    @GetMapping("/Professional/{idPatient}")
    public String professionalPatientTasks(Model model, @PathVariable(name = "idPatient") Long idPatient){
        Long idProf = (Long) session.getAttribute("userId");
        if (!patientRepo.findById(idPatient).get().getProId().equals(idProf)) {
            return "redirect:/Professional";
        }
        model.addAttribute("idP", idPatient);
        model.addAttribute("nameP", patientRepo.findById(idPatient).get().getName());
        model.addAttribute("list", particularTaskRepo.findByPatientIdOrderByDateDayAscTaskTimeAsc(idPatient));
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("now", LocalTime.now());
        return "ProfessionalPatientTasks";
    }

    @GetMapping("/Professional/{idPatient}/taskCreation")
    public String professionaAddTask(Model model, ParticularTask pTask, @PathVariable(name = "idPatient") Long idPatient, @RequestParam(value = "error", required = false) String error){
        Long idProf = (Long) session.getAttribute("userId");
        Long idProfPat = patientRepo.findById(idPatient).get().getProId();
        if (idProf != idProfPat) {
            return "redirect:/Professional";
        }
        model.addAttribute("idP", idPatient);
        model.addAttribute("error", Boolean.valueOf(error));
        return "TaskCreationProfessional";
    }

    @PostMapping("/Professional/{idPatient}/taskCreated")
    public String professionaAddingTask(ParticularTask pTask, @PathVariable(name = "idPatient") Long patientId){
        if (LocalDate.now().isAfter(pTask.getDateDay()) || (LocalTime.now().isAfter(pTask.getTaskTime()) && LocalDate.now().isEqual(pTask.getDateDay()))){
            return "redirect:/Professional/"+patientId+"/taskCreation?error=true";
        }
        pTask.setPatientId(patientId);
        particularTaskRepo.save(pTask);
        return "redirect:/Professional/"+patientId;
    }


    @GetMapping("/Professional/Delete/{idPatient}")
    public String DeletePatient(@PathVariable(name = "idPatient") Long idPatient){
        Long idProf = (Long) session.getAttribute("userId");
        if (!patientRepo.findById(idPatient).get().getProId().equals(idProf)){
            return "redirect:/Professional";
        }
        patientRepo.deleteById(idPatient);
        return "redirect:/Professional";
    }

    @GetMapping("/Professional/{idPatient}/DeleteTask/{idTask}")
    public String DeletePatientTask(@PathVariable(name = "idPatient") Long idPatient, @PathVariable(name = "idTask") Long idTask){
        Long idProf = (Long) session.getAttribute("userId");
        if (!patientRepo.findById(idPatient).get().getProId().equals(idProf)){
            return "redirect:/Professional";
        }
        if(rutineTaskRepo.existsById(idTask)) {
            rutineTaskRepo.deleteById(idTask);
            return "redirect:/Professional/"+idPatient+"/rutineList";
        }
        particularTaskRepo.deleteById(idTask);
        return "redirect:/Professional/"+idPatient;
    }
    
    @GetMapping("Professional/{idPatient}/rutineList")
    public String rutineListProfessional(Model model, @PathVariable(name = "idPatient") Long idPatient){
        Long idProf = (Long) session.getAttribute("userId");
        if (!patientRepo.findById(idPatient).get().getProId().equals(idProf)){
            return "redirect:/Professional";
        }
        model.addAttribute("idP", idPatient);
        model.addAttribute("list", rutineTaskRepo.findByPatientId(idPatient));
        return "rutineListProfessional";
    }

    @GetMapping("Professional/{idPatient}/addRutine")
    public String addRutineProfessional(Model model, @PathVariable(name = "idPatient") Long idPatient, RutineTask rTask){
        Long idProf = (Long) session.getAttribute("userId");
        if (!patientRepo.findById(idPatient).get().getProId().equals(idProf)){
            return "redirect:/Professional";
        }
        model.addAttribute("week", Week.values());
        model.addAttribute("idP", idPatient);
        return "rutineCreationProfessional";
    }
    
    @PostMapping("Professional/{idPatient}/newRutine")
    public String newRutineProfessional(Model model, @PathVariable(name = "idPatient") Long idPatient, RutineTask rTask, @RequestParam List<String> dayList){
        rTask.setWeekDays(dayList);
        rTask.setPatientId(idPatient);
        if (rTask.getWeekDays().contains(LocalDate.now().getDayOfWeek().name()) && rTask.getTaskTime().isAfter(LocalTime.now())){
            particularTaskRepo.save(ParticularTask.addRutine(rTask));
            rutineTaskRepo.save(rTask.updateToday());
            return "redirect:/Professional/"+idPatient+"/rutineList";
        }
        rutineTaskRepo.save(rTask);
        return "redirect:/Professional/"+idPatient+"/rutineList";
    }

    @GetMapping("/Professional/{idPatient}/SwitchRepeat/{idRutine}")
    public String switchRepeat(@PathVariable(name = "idPatient") Long idPatient, @PathVariable(name = "idRutine") Long idRutine){
        Long idProf = (Long) session.getAttribute("userId");
        if (!patientRepo.findById(idPatient).get().getProId().equals(idProf)){
            return "redirect:/Professional";
        }

        rutineTaskRepo.save(rutineTaskRepo.findById(idRutine).get().switchRepeat());
        return "redirect:/Professional/"+idPatient+"/rutineList";
    }

    @GetMapping("/Professional/cancelEmergency/{idPatient}")
    @Transactional
    public String cancelEmergency(@PathVariable(name = "idPatient") Long idPatient) {
        Long profId = (Long) session.getAttribute("userId");
        if(patientRepo.findById(idPatient).get().getProId().equals(profId)) {
            Patient temp = patientRepo.findById(idPatient).get();
            temp.setEmergency(false);
            patientRepo.save(temp);
        }
        return "redirect:/Professional";
    }

    @GetMapping("/afterLogout")
    public String afterLogout(){
        return "redirect:/";
    }

    @Transactional
    public void loadRutines(){
        for (RutineTask r : rutineTaskRepo.findByPatientId((Long) session.getAttribute("userId"))){
            if (r.getToRepeat().equals(true) && (r.getLastUpdated() == null || !r.getLastUpdated().isEqual(LocalDate.now())) && r.getWeekDays().contains(LocalDate.now().getDayOfWeek().name())){
                rutineTaskRepo.save(r.updateToday());
                particularTaskRepo.save(ParticularTask.addRutine(r));
            }
        }
    }

    @GetMapping("/Professional/changePassword/{idPatient}")
    public String changePatientPassword(Model model, @PathVariable(name = "idPatient") Long idPatient) {
        model.addAttribute("idPatient", idPatient);
        return "changePassword";
    }

    @PostMapping("/Professional/PasswordChanged/{idPatient}")
    @Transactional
    public String changePatientPassword(@PathVariable(name = "idPatient") Long idPatient, String password) {
        Long profId = (Long) session.getAttribute("userId");

        if(patientRepo.findById(idPatient).get().getProId().equals(profId)) {
            Patient temp = patientRepo.findById(idPatient).get();
            temp.setPassword(password, new BCryptPasswordEncoder());
            patientRepo.save(temp);
        }
        return "redirect:/Professional";
    }

}
