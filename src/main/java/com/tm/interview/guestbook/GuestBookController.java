package com.tm.interview.guestbook;

import com.tm.interview.guestbook.exception.GuestBookEntryNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.stream.StreamSupport;

@Slf4j
@AllArgsConstructor
@Controller
public class GuestBookController {

    private FileStoreService fileStoreService;
    private GuestBookEntryRepository entryRepository;

    @RequestMapping(path = "/")
    public String index(Model model) {
        Iterable<GuestBookEntry> entries = entryRepository.findAll();
        model.addAttribute("entries", StreamSupport.stream(entries.spliterator(), false).count() > 0 ? entries : null);
        return "index";
    }

    @RequestMapping(path = "/guestbook/add", method = RequestMethod.GET)
    public String showAddEntryForm(Model model) {
        model.addAttribute("entry", GuestBookEntry.builder().build());
        return "add-entry";
    }

    @RequestMapping(path = "/guestbook/edit/{id}", method = RequestMethod.GET)
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        GuestBookEntry entry = entryRepository.findById(id)
                .orElseThrow(GuestBookEntryNotFoundException::new);

        model.addAttribute("entry", entry);
        return "update-entry";
    }

    @RequestMapping(path = "/guestbook/save", method = RequestMethod.POST)
    public String save(@ModelAttribute @Valid GuestBookEntry entry, BindingResult result, Model model) {
        if(result.hasErrors()) {
            model.addAttribute("entry", entry);
            return "add-entry";
        }

        try {
            entry.setImageUrl(fileStoreService.saveFile(entry.getImage()));
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("entry", entry);
            model.addAttribute("msg", "Failed to upload image");
            return "add-entry";
        }

        entryRepository.save(entry);
        return "redirect:/";
    }

    @RequestMapping(path = "/guestbook/update/{id}", method = RequestMethod.POST)
    public String updateEntry(@PathVariable("id") long id, @Valid GuestBookEntry entry, BindingResult result, Model model) {
        if (result.hasErrors()) {
            entry.setId(id);
            model.addAttribute("entry", entry);
            return "update-entry";
        }

        try {
            fileStoreService.deleteFile(entry.getImageUrl());
            entry.setImageUrl(fileStoreService.saveFile(entry.getImage()));
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("entry", entry);
            model.addAttribute("msg", "Failed to upload image");
            return "update-entry";
        }

        entryRepository.save(entry);
        return "redirect:/";

    }

    @RequestMapping(path = "/guestbook/delete/{id}", method = RequestMethod.GET)
    public String delete(@PathVariable Long id) {
        GuestBookEntry entry = entryRepository.findById(id)
                .orElseThrow(GuestBookEntryNotFoundException::new);
        try {
            fileStoreService.deleteFile(entry.getImageUrl());
        } catch (IOException e) {
            log.debug("file not found");
        }
        entryRepository.deleteById(id);

        return "redirect:/";
    }

    @RequestMapping(path = "/guestbook/approve/{id}", method = RequestMethod.GET)
    public String approve(@PathVariable Long id) {
        GuestBookEntry entry = entryRepository.findById(id)
                .orElseThrow(GuestBookEntryNotFoundException::new);
        entry.setApproved(true);
        entryRepository.save(entry);
        return "redirect:/";
    }
}