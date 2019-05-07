package com.tm.interview.guestbook;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GuestBookEntryRepository extends CrudRepository<GuestBookEntry, Long> {

    List<GuestBookEntry> findTop50ByOrderByCreatedDateDesc();
}
