package com.contect.hello.controller;

import com.contect.hello.domain.*;
import com.contect.hello.service.PatientService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @Value("${file.upload.path}")
    private String fileDir;


    @GetMapping("/api/patients")
    @ResponseBody
    public ResponseEntity<Page<PatientListResponse>> getPatientApi(
            PatientSearchCond cond,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Patient> patientList = patientService.getPatientList(cond, pageable);

        Page<PatientListResponse> patientListResponsePage = patientList.map(PatientListResponse::from);

        return ResponseEntity.ok(patientListResponsePage);
    }


    @PostMapping("/patients")
    public String save(@Valid @ModelAttribute("formData") PatientForm formData, BindingResult bindingResult, Model model) throws IOException {
        log.info("formData={}", formData.toString());
        if(bindingResult.hasErrors()) {
            model.addAttribute("patients", patientService.findAll());
            model.addAttribute("hasError", true);
            return "patients";
        }
        MultipartFile imageFile = formData.getImageFile();
        String storedFileName = null;

        if(!imageFile.isEmpty()) {
            // 1.파일 저장 경로 설정 (예: C:/upload/ 또는 /Users/유저명/upload/)
            File dir = new File(fileDir);
            if (!dir.exists()) dir.mkdirs();

            // 2. 파일명 중복 방지를 위해 UUID 생성
            String originalFilename = imageFile.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            storedFileName = uuid + extension;

            imageFile.transferTo(new File(fileDir + storedFileName));
        }
        Patient patient = new Patient(formData.getName(),formData.getBirth(), formData.getGender(), formData.getCn(), storedFileName);
        patientService.save(patient);
        return "redirect:/patients";
    }

    @GetMapping("/patients/{id}")
    public String findById(@PathVariable(name = "id") Long id, Model model) {
        PatientDetailResponse patientDetailResponse = patientService.findById(id);
        log.info("Patient={}",patientDetailResponse);
        model.addAttribute("patient", patientDetailResponse);
        return "patientDetail";
    }

    @GetMapping("/api/patients/{id}")
    @ResponseBody
    public ResponseEntity<PatientDetailResponse> getFindById(@PathVariable(name = "id") Long id) {
        PatientDetailResponse patientDetailResponse = patientService.findById(id);
        return ResponseEntity.ok(patientDetailResponse);
    }

    @PutMapping(value = "/api/patients/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> updatePatient(
            @PathVariable Long id,
            @RequestPart("patient") PatientForm dto, // Blob으로 보낸 JSON이 이 객체로 자동 변환됨
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        patientService.update(id, dto, file);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/patients/{id}")
    @ResponseBody
    public ResponseEntity<Void> updateApi(@PathVariable(name = "id") Long id,
                                      @RequestBody PatientForm patientForm
                                      ) throws IOException {
        patientService.update(id, patientForm, null);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/patients/{id}")
    public String update(@PathVariable(name = "id") Long id,
                         @Valid @ModelAttribute("patient") PatientForm updateForm,
                         BindingResult bindingResult,
                         Model model) throws IOException {
        if(bindingResult.hasErrors()) {
            model.addAttribute("hasError", true);
            return "patientDetail";
        }

        patientService.update(id, updateForm, null);
//        model.addAttribute("patient", patient);
        return "redirect:/patients/" + id;
    }

    @DeleteMapping("/api/patients/{id}")
    @ResponseBody
    public ResponseEntity<Void> deletePatient(@PathVariable(name = "id") Long id) {
        patientService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/patients/{id}/delete")
    public String delete(@PathVariable(name = "id") Long id) {
        patientService.delete(id);
        return "redirect:/patients";
    }

    @PostMapping("/api/patients/{id}/memos")
    public ResponseEntity<PatientDetailResponse> saveMemo(@PathVariable(name = "id") Long id,
                                               @RequestBody Map<String,String> body) {

        String content = body.get("content");
        log.info("id={} memo={}", id, content);
        patientService.addMemo(id,content);

        PatientDetailResponse patientDetailResponse = patientService.findById(id);
        return ResponseEntity.ok(patientDetailResponse);
    }

    @PutMapping("/api/memos/{memoId}")
    public ResponseEntity<PatientDetailResponse> updateMemo(
            @PathVariable Long memoId,
            @RequestBody Map<String, String> body
    ) {
        //1.수정 실행
        Long patientId = patientService.updateMemo(memoId, body.get("content"));

        //2. 수정된 후의 환자 정보를 다시 조회해서 반환 (화면 갱신용)
        return ResponseEntity.ok(patientService.findById(patientId));
    }

    @DeleteMapping("/api/memos/{id}")
    public ResponseEntity<PatientDetailResponse> deleteMemo(@PathVariable(name = "id") Long memoId) {
        Long patientId = patientService.deleteMemo(memoId);
        PatientDetailResponse patientDetailResponse = patientService.findById(patientId);
        return ResponseEntity.ok(patientDetailResponse);
    }



    /**
     * 테스트용 데이터 추가
     */
//    @PostConstruct
//    public void init() {
//        if (patientService.count() == 0) {
//            patientService.save(new PatientForm("오민규", "19940421", "male", "무릎 타박상",""));
//            patientService.save(new PatientForm("방유경", "19891127", "female", "무릎 귀여움상",""));
//        }
//    }


}
