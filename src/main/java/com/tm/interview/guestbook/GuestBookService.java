package com.tm.interview.guestbook;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
class GuestBookService {

    private GuestBookEntryRepository entryRepository;

    List<GuestBookEntry> getRecentEntries() {
        return entryRepository.findTop50ByOrderByCreatedDateDesc();
    }
}
