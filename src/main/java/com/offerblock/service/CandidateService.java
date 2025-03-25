package com.offerblock.service;

import java.util.List;
import com.offerblock.dto.CandidateSignup;
import com.offerblock.entity.Candidate;

public interface CandidateService {

    Candidate save(CandidateSignup candidateSignup);

    List<Candidate> getAllCandidates();

    Candidate getCandidateById(String candidateId);

    void deleteById(String candidateId);

    void assignRecruiterRole(String candidateId);
}
