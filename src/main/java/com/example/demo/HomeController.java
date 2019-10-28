package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.graalvm.compiler.nodes.memory.MemoryCheckpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    MessageRepository messageRepository;

    @RequestMapping("/")
    public String homePage(Model m){
        m.addAttribute("messages", messageRepository.findAll());
        return "list";
    }

    @GetMapping("/add")
    public String messageForm(Model m){
        m.addAttribute("message", new Message());
        return "messageform";
    }

    @PostMapping("/process")
    public String processForm(@Valid Message message,
        BindingResult result){
    if (result.hasErrors()){
        return "messageform";
    }
    messageRepository.save(message);
    return "redirect:/";
    }

    @RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model m)
    {
        m.addAttribute("message", messageRepository.findById(id).get());
        return "show";
    }
    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model m){
        m.addAttribute("message", messageRepository.findById(id).get());
        return "messageform";
    }
    @RequestMapping("/delete/{id}")
    public String delMessage(@PathVariable("id") long id){
        messageRepository.deleteById(id);
        return "redirect:/";
    }

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String homepage(Model m){
        m.addAttribute("messages", messageRepository.findAll());
        return "list";
    }
    @GetMapping("/add")
    public String messageform(Model m){
        m.addAttribute("message", new Message());
        return "form";
    }
    @PostMapping("/add")
    public String processForm(@ModelAttribute Message message,
                              @RequestParam("file")MultipartFile file){
        if (file.isEmpty()){
         return "redirect:/add";
        }
        try{
            Map uploadResult = cloudc.upload(file.getBytes(),
                    ObjectUtils.asMap("resourcetype", "auto"));
            message.setHeadshot(uploadResult.get("url").toString());
            messageRepository.save(message);
        } catch (IOException e){
            e.printStackTrace();
            return "redirect:/add";
        }
        return "redirect:/";
    }
}

