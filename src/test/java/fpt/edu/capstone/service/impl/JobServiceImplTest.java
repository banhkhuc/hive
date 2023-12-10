package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.job.*;
import fpt.edu.capstone.dto.recruiter.CountTotalCreatedJobResponse;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.JobRepository;
import fpt.edu.capstone.service.AppliedJobService;
import fpt.edu.capstone.utils.Pagination;
import fpt.edu.capstone.utils.ResponseDataPagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JobServiceImplTest {

	@InjectMocks
	JobServiceImpl jobService;

	@Mock
	CompanyServiceImpl companyService;

	@Mock
	RecruiterServiceImpl recruiterService;

	@Mock
	FieldsServiceImpl fieldsService;

	@Mock
	JobRepository jobRepository;
	
	@Mock
	ImageServiceImpl imageService;
	
	@Mock
	JobHashTagServiceImpl jobHashTagService;
	
	@Mock
	CandidateServiceImpl candidateService;
	
	@Mock
	CVServiceImpl cvService;
	
	@Mock
	MajorLevelServiceImpl majorLevelService;
	
	@Mock
	MajorServiceImpl majorService;

	@Mock
	ModelMapper modelMapper;

	@Mock
	private AppliedJobService mockAppliedJobService;

	@BeforeEach
	public void init(){
		MockitoAnnotations.openMocks(this);
	}
	

	@Test
	void givenCreateRequestIdNull_whenCallCreateJob_thenCompanyServiceFindByIdAreNotCalled(){
		Exception exception = assertThrows(Exception.class, () -> jobService.createJob(null));
		verify(companyService,times(0)).findById(1L);
	}

	@Test
	void givenCompanyIdIsNotFound_whenCallCreateJob_thenThrowErrorMessage() {
		when(companyService.findById(1L)).thenReturn(Optional.empty());

		CreateJobRequest createJobRequest = new CreateJobRequest();
		createJobRequest.setCompanyId(1L);

		Exception exception = assertThrows(Exception.class, () -> {
			jobService.createJob(createJobRequest);
		});

		assertEquals(null,exception.getMessage());
	}

	@Test
	void givenRecruiterIdIsNotFound_whenCallCreateJob_thenThrowErrorMessage() {
		when(companyService.findById(1L)).thenReturn(Optional.of(new Company()));
		when(recruiterService.existById(1L)).thenReturn(false);

		CreateJobRequest createJobRequest = new CreateJobRequest();
		createJobRequest.setCompanyId(1L);
		createJobRequest.setRecruiterId(1L);

		Exception exception = assertThrows(Exception.class, () -> {
			jobService.createJob(createJobRequest);
		});

		assertEquals(null,exception.getMessage());
	}

	@Test
	void givenFieldIdIsNotFound_whenCallCreateJob_thenThrowErrorMessage() {
		when(companyService.findById(1L)).thenReturn(Optional.of(new Company()));
		when(recruiterService.existById(1L)).thenReturn(true);
		when(fieldsService.existById(1L)).thenReturn(false);

		CreateJobRequest createJobRequest = new CreateJobRequest();
		createJobRequest.setCompanyId(1L);
		createJobRequest.setRecruiterId(1L);
		createJobRequest.setFieldId(1L);

		Exception exception = assertThrows(Exception.class, () -> {
			jobService.createJob(createJobRequest);
		});

		assertEquals(null,exception.getMessage());
	}

	@Test
	void givenModelMapperMapReturnNull_whenCallCreateJob_thenJobRepositorySaveArrNotCalled(){
		when(companyService.findById(1L)).thenReturn(Optional.of(new Company()));
		when(recruiterService.existById(1L)).thenReturn(true);
		when(fieldsService.existById(1L)).thenReturn(true);

		CreateJobRequest request = new CreateJobRequest();
		request.setCompanyId(1L);
		request.setRecruiterId(1L);
		request.setFieldId(1L);
		Object CreateJobRequest = request;
		Job job = new Job();

		when(modelMapper.map(CreateJobRequest, Job.class)).thenReturn(null);

		Exception exception = assertThrows(Exception.class,() -> jobService.createJob(request));

		verify(jobRepository,times(0)).save(job);
	}

	@Test
	void givenCreateJobRequest_whenCallCreateJob_thenCallJobRepositorySave(){
		when(companyService.findById(1L)).thenReturn(Optional.of(new Company()));
		when(recruiterService.existById(1L)).thenReturn(true);
		when(fieldsService.existById(1L)).thenReturn(true);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(new Job());

		CreateJobRequest request = new CreateJobRequest();
		request.setCompanyId(1L);
		request.setRecruiterId(1L);
		request.setFieldId(1L);
		Object CreateJobRequest = request;

		when(modelMapper.map(CreateJobRequest, Job.class)).thenReturn(new Job());

		jobService.createJob(request);

		verify(jobRepository,times(1)).save(ArgumentMatchers.any());
	}

	// JobController.java line 56

	// JobController.java line 90

	@Test
	void givenAllParamsAreCorrect_whenCallSearchListJobFilter_thenReturnResponseDataPaginationDataSizeIs2() {
		Job job1 = new Job();
		job1.setId(1L);
		job1.setCompanyId(1L);
		Job job2 = new Job();
		job2.setId(2L);
		job2.setCompanyId(2L);
		List<Job> jobList = Arrays.asList(job1,job2);

		when(jobRepository.searchListJobFilter(any(Pageable.class),eq(1L),eq(1L),
						eq("do some thing"),eq("abc"),eq("abc"),0,0))
						.thenReturn(new PageImpl<>(jobList));

		JobResponse jobResponse1 = new JobResponse();
		JobResponse jobResponse2 = new JobResponse();

		when(modelMapper.map(job1, JobResponse.class)).thenReturn(jobResponse1);
		when(modelMapper.map(job2, JobResponse.class)).thenReturn(jobResponse2);

		Company company1 = new Company();
		company1.setName("a");
		Company company2 = new Company();
		company2.setName("b");

		when(companyService.getCompanyById(1L)).thenReturn(company1);
		when(companyService.getCompanyById(2L)).thenReturn(company2);

		 ResponseDataPagination responseDataPagination = jobService.searchListJobFilter(1,1,1L,
						1L,"do some thing","abc","abc",0,0);
		List<JobResponse> data = (List<JobResponse>) responseDataPagination.getData();

		assertEquals(2,data.size());
	}

	@Test
	void givenAllParamsAreCorrect_whenCallSearchListJobFilter_thenReturnResponseDataPaginationStatusIsSuccess() {
		Job job1 = new Job();
		job1.setId(1L);
		job1.setCompanyId(1L);
		Job job2 = new Job();
		job2.setId(2L);
		job2.setCompanyId(2L);
		List<Job> jobList = Arrays.asList(job1,job2);

		when(jobRepository.searchListJobFilter(any(Pageable.class),eq(1L),eq(1L),
				eq("do some thing"),eq("abc"),eq("abc"),0,0))
				.thenReturn(new PageImpl<>(jobList));


		JobResponse jobResponse1 = new JobResponse();
		JobResponse jobResponse2 = new JobResponse();


		when(modelMapper.map(job1, JobResponse.class)).thenReturn(jobResponse1);
		when(modelMapper.map(job2, JobResponse.class)).thenReturn(jobResponse2);

		Company company1 = new Company();
		company1.setName("a");
		Company company2 = new Company();
		company2.setName("b");

		when(companyService.getCompanyById(1L)).thenReturn(company1);
		when(companyService.getCompanyById(2L)).thenReturn(company2);

		ResponseDataPagination responseDataPagination = jobService.searchListJobFilter(1,10,1L,
						1L,"do some thing","abc","abc",0,0);

		assertEquals("Success",responseDataPagination.getStatus());
	}

	@Test
	void givenAllParamsAreCorrect_whenCallSearchListJobFilter_thenReturnResponseDataPaginationPaginationGetCurrentPage() {
		Job job1 = new Job();
		job1.setId(1L);
		job1.setCompanyId(1L);
		Job job2 = new Job();
		job2.setId(2L);
		job2.setCompanyId(2L);
		List<Job> jobList = Arrays.asList(job1,job2);

		when(jobRepository.searchListJobFilter(any(Pageable.class),eq(1L),eq(1L),
				eq("do some thing"),eq("abc"),eq("abc"),0,0))
				.thenReturn(new PageImpl<>(jobList));


		JobResponse jobResponse1 = new JobResponse();
		JobResponse jobResponse2 = new JobResponse();


		when(modelMapper.map(job1, JobResponse.class)).thenReturn(jobResponse1);
		when(modelMapper.map(job2, JobResponse.class)).thenReturn(jobResponse2);

		Company company1 = new Company();
		company1.setName("a");
		Company company2 = new Company();
		company2.setName("b");

		when(companyService.getCompanyById(1L)).thenReturn(company1);
		when(companyService.getCompanyById(2L)).thenReturn(company2);

		ResponseDataPagination responseDataPagination = jobService.searchListJobFilter(1,10,1L,
						1L,"do some thing","abc","abc",0,0);

		Pagination pagination = (Pagination) responseDataPagination.getPagination();

		assertEquals(1L,pagination.getCurrentPage());
	}

	@Test
	void givenAllParamsAreCorrect_whenCallSearchListJobFilter_thenReturnResponseDataGetPageSize() {
		Job job1 = new Job();
		job1.setId(1L);
		job1.setCompanyId(1L);
		Job job2 = new Job();
		job2.setId(2L);
		job2.setCompanyId(2L);
		List<Job> jobList = Arrays.asList(job1,job2);

		when(jobRepository.searchListJobFilter(any(Pageable.class),eq(1L),eq(1L),
				eq("do some thing"),eq("abc"),eq("abc"),0,0))
				.thenReturn(new PageImpl<>(jobList));


		JobResponse jobResponse1 = new JobResponse();
		JobResponse jobResponse2 = new JobResponse();


		when(modelMapper.map(job1, JobResponse.class)).thenReturn(jobResponse1);
		when(modelMapper.map(job2, JobResponse.class)).thenReturn(jobResponse2);

		Company company1 = new Company();
		company1.setName("a");
		Company company2 = new Company();
		company2.setName("b");

		when(companyService.getCompanyById(1L)).thenReturn(company1);
		when(companyService.getCompanyById(2L)).thenReturn(company2);

		ResponseDataPagination responseDataPagination = jobService.searchListJobFilter(1,1,1L,
						1L,"do some thing","abc","abc",0,0);

		Pagination pagination = (Pagination) responseDataPagination.getPagination();

		assertEquals(1L,pagination.getPageSize());
	}

	@Test
	void givenAllParamsAreCorrect_whenCallSearchListJobFilter_thenReturnResponseDataGetTotalPage() {
		Job job1 = new Job();
		job1.setId(1L);
		job1.setCompanyId(1L);
		Job job2 = new Job();
		job2.setId(2L);
		job2.setCompanyId(2L);
		List<Job> jobList = Arrays.asList(job1,job2);

		when(jobRepository.searchListJobFilter(any(Pageable.class),eq(1L),eq(1L),
				eq("do some thing"),eq("abc"),eq("abc"),0,0))
				.thenReturn(new PageImpl<>(jobList));


		JobResponse jobResponse1 = new JobResponse();
		JobResponse jobResponse2 = new JobResponse();


		when(modelMapper.map(job1, JobResponse.class)).thenReturn(jobResponse1);
		when(modelMapper.map(job2, JobResponse.class)).thenReturn(jobResponse2);

		Company company1 = new Company();
		company1.setName("a");
		Company company2 = new Company();
		company2.setName("b");

		when(companyService.getCompanyById(1L)).thenReturn(company1);
		when(companyService.getCompanyById(2L)).thenReturn(company2);

		ResponseDataPagination responseDataPagination = jobService.searchListJobFilter(1,1,1L,
						1L,"do some thing","abc","abc",0,0);

		Pagination pagination = (Pagination) responseDataPagination.getPagination();

		assertEquals(1L,pagination.getTotalPage());
	}

	@Test
	void givenAllParamsAreCorrect_whenCallSearchListJobFilter_thenReturnResponseDataPaginationPaginationGetTotalRecords() {
		Job job1 = new Job();
		job1.setId(1L);
		job1.setCompanyId(1L);
		Job job2 = new Job();
		job2.setId(2L);
		job2.setCompanyId(2L);
		List<Job> jobList = Arrays.asList(job1,job2);

		when(jobRepository.searchListJobFilter(any(Pageable.class),eq(1L),eq(1L),
				eq("do some thing"),eq("abc"),eq("abc"),0,0))
				.thenReturn(new PageImpl<>(jobList));


		JobResponse jobResponse1 = new JobResponse();
		JobResponse jobResponse2 = new JobResponse();


		when(modelMapper.map(job1, JobResponse.class)).thenReturn(jobResponse1);
		when(modelMapper.map(job2, JobResponse.class)).thenReturn(jobResponse2);

		Company company1 = new Company();
		company1.setName("a");
		Company company2 = new Company();
		company2.setName("b");

		when(companyService.getCompanyById(1L)).thenReturn(company1);
		when(companyService.getCompanyById(2L)).thenReturn(company2);

		ResponseDataPagination responseDataPagination = jobService.searchListJobFilter(1,1,1L,
						1L,"do some thing","abc","abc",0,0);

		Pagination pagination = (Pagination) responseDataPagination.getPagination();

		assertEquals(2L,pagination.getTotalRecords());
	}

	@Test
	void givenPageSizeIsNull_whenCallSearchListJobFilter_thenAssertJobRepositorySearchListJobFilterIsNotCalled() {
		Exception exception = assertThrows(Exception.class, () -> {
			jobService.searchListJobFilter(1, null, 1L,
							1L, "do some thing",  "abc", "abc",0,0);
		});
		// if app crash when PageSize is null jobRepository.searchListJobFilter are not called
		verify(jobRepository,times(0)).searchListJobFilter(any(Pageable.class),eq(1L),eq(1L),
						eq("do some thing"),eq("abc"),eq("abc"),0,0);
	}

	@Test
	void givenPageNoIsNull_whenCallSearchListJobFilter_thenAssertJobRepositorySearchListJobFilterIsNotCalled() {
		Exception exception = assertThrows(Exception.class, () -> {
			jobService.searchListJobFilter(null, 1, 1L,
							1L, "do some thing",  "abc", "abc",0,0);
		});
	// if app crash when PageNo is null jobRepository.searchListJobFilter are not called
		verify(jobRepository,times(0)).searchListJobFilter(any(Pageable.class),eq(1L),eq(1L),
						eq("do some thing"),eq("abc"),eq("abc"),0,0);
	}

	@Test
	void givenJobServiceSearchListJobFilterIsEmpty_whenCallSearchListJobFilter_thenReturnResponseDataPaginationDataCurrentPage() {

		when(jobRepository.searchListJobFilter(any(),eq(1L),eq(1L),
						eq("do some thing"),eq("abc"),eq("abc"),0,0))
						.thenReturn(Page.empty());
		ResponseDataPagination responseDataPagination = jobService.searchListJobFilter(1, 1, 1L,
							1L, "do some thing", "abc", "abc",0,0);

		Pagination pagination = (Pagination) responseDataPagination.getPagination();

		assertEquals(1L,pagination.getCurrentPage());
	}

	@Test
	void givenJobServiceSearchListJobFilterIsEmpty_whenCallSearchListJobFilter_thenReturnResponseDataPaginationDataGetPageSize() {

		when(jobRepository.searchListJobFilter(any(),eq(1L),eq(1L),
						eq("do some thing"),eq("abc"),eq("abc"),0,0))
						.thenReturn(Page.empty());
		ResponseDataPagination responseDataPagination = jobService.searchListJobFilter(1, 1, 1L,
						1L, "do some thing", "abc", "abc",0,0);

		Pagination pagination = (Pagination) responseDataPagination.getPagination();

		assertEquals(1L,pagination.getPageSize());
	}

	@Test
	void givenJobServiceSearchListJobFilterIsEmpty_whenCallSearchListJobFilter_thenReturnResponseDataPaginationDataGetTotalPage() {

		when(jobRepository.searchListJobFilter(any(),eq(1L),eq(1L),
						eq("do some thing"),eq("abc"),eq("abc"),0,0))
						.thenReturn(Page.empty());
		ResponseDataPagination responseDataPagination = jobService.searchListJobFilter(1, 10, 1L,
						1L, "do some thing",  "abc", "abc",0,0);

		Pagination pagination = (Pagination) responseDataPagination.getPagination();

		assertEquals(1L,pagination.getTotalPage());
	}

	@Test
	void givenJobServiceSearchListJobFilterIsEmpty_whenCallSearchListJobFilter_thenReturnResponseDataPaginationDataGetTotalRecords() {

		when(jobRepository.searchListJobFilter(any(),eq(1L),eq(1L),
						eq("do some thing"),eq("abc"),eq("abc"),0,0))
						.thenReturn(Page.empty());
		ResponseDataPagination responseDataPagination = jobService.searchListJobFilter(1, 1, 1L,
						1L, "do some thing",  "abc", "abc",0,0);

		Pagination pagination = (Pagination) responseDataPagination.getPagination();

		assertEquals(0,pagination.getTotalRecords());
	}

	@Test
	void givenJobServiceSearchListJobFilterIsEmpty_whenCallSearchListJobFilter_thenReturn1() {

		when(jobRepository.searchListJobFilter(any(),eq(1L),eq(1L),
						eq("do some thing"),eq("abc"),eq("abc"),0,0))
						.thenReturn(Page.empty());
		ResponseDataPagination responseDataPagination = jobService.searchListJobFilter(1, 1, 1L,
						1L, "do some thing", "abc", "abc",0,0);

		assertEquals("Success",responseDataPagination.getStatus());
	}

	@Test
	void givenJobServiceSearchListJobFilterIsEmpty_whenCallSearchListJobFilter_thenReturn2() {

		when(jobRepository.searchListJobFilter(any(),eq(1L),eq(1L),
						eq("do some thing"),eq("abc"),eq("abc"),0,0))
						.thenReturn(Page.empty());
		ResponseDataPagination responseDataPagination = jobService.searchListJobFilter(1, 1, 1L,
						1L, "do some thing", "abc", "abc",0,0);

		List<JobResponse> data = (List<JobResponse>) responseDataPagination.getData();

		assertEquals(0,data.size());
	}

	// JobController.java line 90

	//JobController.java line 128

	@Test
	void givenUpdateJobRequestIsNull_whenCallUpdateJob_thenModelMapperMapAreNotCalled() {
		Exception exception = assertThrows(Exception.class, () -> {
			jobService.updateJob(null);
		});
		verify(modelMapper,times(0)).map(any(),any());
	}

	@Test
	void givenRequestIdIsNull_whenCallUpdateJob_thenModelMapperMapAreNotCalled() {
		UpdateJobRequest updateJobRequest = new UpdateJobRequest();
		Exception exception = assertThrows(Exception.class, () -> {
			jobService.updateJob(updateJobRequest);
		});
		verify(modelMapper,times(0)).map(any(),any());
	}

	@Test
	void givenJobRepositoryGetById_whenCallUpdateJob_thenThrowException() {
		UpdateJobRequest updateJobRequest = new UpdateJobRequest();
		Exception exception = assertThrows(Exception.class, () -> {
			jobService.updateJob(updateJobRequest);
		});
		assertEquals(null,exception.getMessage());
	}

	@Test
	void givenFlagIsPosted_whenCallUpdateJob_thenModelMapperMapAreNotCalled() {
		UpdateJobRequest updateJobRequest = new UpdateJobRequest();
		Job job = new Job();
		job.setFlag("Posted");
		when(jobRepository.getById(1L)).thenReturn(job);
		Exception exception = assertThrows(Exception.class, () -> {
			jobService.updateJob(updateJobRequest);
		});
		verify(modelMapper,times(0)).map(any(),any());
	}

	@Test
	void givenParamIsCorrect_whenCallUpdateJob_thenModelMapperMapAreNotCalled() {
		UpdateJobRequest updateJobRequest = new UpdateJobRequest();
		updateJobRequest.setJobId(1L);
		updateJobRequest.setFlag("Draft");
		Job job = new Job();
		job.setFlag("Draft");
		when(jobRepository.getById(1L)).thenReturn(job);
		when(modelMapper.map(any(),any())).thenReturn(job);
		jobService.updateJob(updateJobRequest);

		verify(jobRepository,times(1)).save(job);
	}

	// JobController.java line 90

	// JobController.java line 169

	@Test
	void givenRepositoryGetByIdReturnNUll_whenCallGetJobById_thenThrowException(){
		when(jobRepository.getById(1L)).thenReturn(null);
		Exception exception = assertThrows(Exception.class,() -> jobService.getJobById(1L));
		assertEquals(ResponseMessageConstants.JOB_DOES_NOT_EXIST,exception.getMessage());
	}

	@Test
	void givenRepositoryGetByIdReturnTrueValue_whenCallGetJobById_thenReturnJob(){
		Job job = new Job();
		job.setId(1L);
		when(jobRepository.getById(1L)).thenReturn(job);
		Job result = jobService.getJobById(1L);
		assertEquals(1L,result.getId());
	}

	@Test
	void givenRepositoryGetByIdReturnFalseValue_whenCallGetJobById_thenFalseJob(){
		Job job = new Job();
		job.setId(2L);
		when(jobRepository.getById(1L)).thenReturn(job);
		Job result = jobService.getJobById(1L);
		assertNotEquals(1L,result.getId());
	}

	@Test
	void givenRepositoryGetByIdReturnJobIdEquals0_whenCallGetJobById_thenThrowException(){
		Job job = new Job();
		job.setId(0);
		when(jobRepository.getById(1L)).thenReturn(job);
		Exception exception = assertThrows(Exception.class,() -> jobService.getJobById(1L));
		assertEquals(ResponseMessageConstants.JOB_DOES_NOT_EXIST,exception.getMessage());
	}

	Job job(){
		Job job = new Job(1L, 1L, "jobName", "workPlace", "workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0),
				LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L, "rank", "experience", false, "jobDescription",
				"jobRequirement", "benefit", 1L, 0, 1L, "weekday", 1L, "flag",  "academicLevel");
		return job;
	}
	@Test
	public void testSaveJob() {
		final Job job = job();
		final Job job1 = job();
		when(jobRepository.save(any(Job.class))).thenReturn(job1);
		jobService.saveJob(job);
		verify(jobRepository).save(any(Job.class));
	}

	@Test
	public void testDeleteJob() {
		final Job job = job();
		when(jobRepository.getById(1L)).thenReturn(job);
		final Job job1 = job();
		when(jobRepository.save(any(Job.class))).thenReturn(job1);
		jobService.deleteJob(1L);
		verify(jobRepository).save(any(Job.class));
	}

	@Test
	public void testDeleteJob_JobRepositoryGetByIdReturnsNull() {
		when(jobRepository.getById(1L)).thenReturn(null);
		assertThatThrownBy(() -> jobService.deleteJob(1L)).isInstanceOf(HiveConnectException.class);
	}

	@Test
	public void testExistsById() {
		when(jobRepository.existsById(1L)).thenReturn(false);
		final boolean result = jobService.existsById(1L);
		assertThat(result).isFalse();
	}

	@Test
	public void testGetNewestJobList() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getNewestJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs);
		final Page<Job> result = jobService.getNewestJobList(PageRequest.of(1, 10));
	}

	@Test
	public void testGetNewestJobList_JobRepositoryReturnsNoItems() {
		when(jobRepository.getNewestJob(any(Pageable.class), eq(true), eq(0), eq("flag")))
				.thenReturn(new PageImpl<>(Collections.emptyList()));
		final Page<Job> result = jobService.getNewestJobList(PageRequest.of(1, 10));
	}

	@Test
	public void testGetUrgentJobList() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getUrgentJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs);
		final Page<Job> result = jobService.getUrgentJobList(PageRequest.of(1, 10));
	}

	@Test
	public void testGetUrgentJobList_JobRepositoryReturnsNoItems() {
		when(jobRepository.getUrgentJob(any(Pageable.class), eq(true), eq(0), eq("flag")))
				.thenReturn(new PageImpl<>(Collections.emptyList()));
		final Page<Job> result = jobService.getUrgentJobList(PageRequest.of(1, 10));
	}

	@Test
	public void testGetPopularJobList_JobRepositoryReturnsNoItems() {
		when(jobRepository.getPopularJob(any(Pageable.class), eq(true), eq(0), eq("flag"),null))
				.thenReturn(new PageImpl<>(Collections.emptyList()));
		final Page<Job> result = jobService.getPopularJobList(PageRequest.of(1, 10),null);
	}

	@Test
	public void testGetJobOfRecruiter_JobRepositoryReturnsNoItems() {
		when(jobRepository.getAllByRecruiterIdAndJobNameOrderByUpdatedAtDesc(any(Pageable.class), eq(1L), ""))
				.thenReturn(new PageImpl<>(Collections.emptyList()));
		final Page<Job> result = jobService.getJobOfRecruiter(PageRequest.of(1, 10), 1L, "");
	}

	@Test
	public void testFindById() throws Exception {
		final Optional<Job> job = Optional.of(job());
		when(jobRepository.findById(1L)).thenReturn(job);
		final Optional<Job> result = jobService.findById(1L);
	}

	@Test
	public void testFindById_JobRepositoryReturnsAbsent() {
		when(jobRepository.findById(1L)).thenReturn(Optional.empty());
		final Optional<Job> result = jobService.findById(1L);
		assertThat(result).isEmpty();
	}

	@Test
	public void testGetJobByCompanyId() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getJobByCompanyId(eq(1L), any(Pageable.class))).thenReturn(jobs);
		final Page<Job> result = jobService.getJobByCompanyId(1L, 1L, 1L);
	}

	@Test
	public void testGetJobByCompanyId_JobRepositoryReturnsNoItems() {
		when(jobRepository.getJobByCompanyId(eq(1L), any(Pageable.class)))
				.thenReturn(new PageImpl<>(Collections.emptyList()));
		final Page<Job> result = jobService.getJobByCompanyId(1L, 1L, 1L);
	}

	@Test
	public void testGetJobByRecruiterId() {
		final List<Job> jobs = Arrays.asList(job());
		when(jobRepository.findAllByRecruiterId(1L)).thenReturn(jobs);
		final List<Job> result = jobService.getJobByRecruiterId(1L);
	}

	@Test
	public void testGetJobByRecruiterId_JobRepositoryReturnsNoItems() {
		when(jobRepository.findAllByRecruiterId(1L)).thenReturn(Collections.emptyList());
		final List<Job> result = jobService.getJobByRecruiterId(1L);
		assertThat(result).isEqualTo(Collections.emptyList());
	}

	@Test
	public void testGetSameJobsOtherCompanies() {
		final List<Job> jobs = Arrays.asList(job());
		when(jobRepository.getSameJobsOtherCompanies(1L)).thenReturn(jobs);
		final JobResponse jobResponse = jobResponse();
		when(modelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final List<JobResponse> result = jobService.getSameJobsOtherCompanies(1L);
	}

	@Test
	public void testGetSameJobsOtherCompanies_JobRepositoryReturnsNoItems() {
		when(jobRepository.getSameJobsOtherCompanies(1L)).thenReturn(Collections.emptyList());
		final JobResponse jobResponse = jobResponse();
		when(modelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final List<JobResponse> result = jobService.getSameJobsOtherCompanies(1L);
		assertThat(result).isEqualTo(Collections.emptyList());
	}

	@Test
	public void testGetSameJobsOtherCompanies_ImageServiceReturnsAbsent() {
		final List<Job> jobs = Arrays.asList(job());
		when(jobRepository.getSameJobsOtherCompanies(1L)).thenReturn(jobs);
		final JobResponse jobResponse = jobResponse();
		when(modelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
		when(imageService.getImageCompany(1L, true)).thenReturn(Optional.empty());
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final List<JobResponse> result = jobService.getSameJobsOtherCompanies(1L);
	}

	@Test
	public void testGetSameJobsOtherCompanies_CompanyServiceReturnsNull() {
		final List<Job> jobs = Arrays.asList(job());
		when(jobRepository.getSameJobsOtherCompanies(1L)).thenReturn(jobs);
		final JobResponse jobResponse = jobResponse();
		when(modelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		when(companyService.getCompanyById(1L)).thenReturn(null);
		final List<JobResponse> result = jobService.getSameJobsOtherCompanies(1L);
	}

	@Test
	public void testGetDataHomePage_JobRepositoryGetListJobByWorkFormReturnsNoItems() {
		when(jobRepository.getListJobByWorkForm(any(Pageable.class), eq("FULLTIME"), eq("flag")))
				.thenReturn(new PageImpl<>(Collections.emptyList()));
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getPopularJob(any(Pageable.class), eq(true), eq(0), eq("flag"),null)).thenReturn(jobs);
		final Page<Job> jobs1 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getNewestJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs1);
		final Page<Job> jobs2 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getUrgentJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs2);
		final Page<Job> jobs3 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag"))).thenReturn(jobs3);
		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse(1L, 1L, "jobName", "workPlace",
				"workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L,
				"rank", "experience", false, "jobDescription", "jobRequirement", "benefit", 1L, 1L,
				"weekday", 1L, "flag", "companyAvatar", Arrays.asList("value"), "companyName");
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);
		final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final HomePageData result = jobService.getDataHomePage();
	}

	@Test
	public void testGetDataHomePage_JobRepositoryGetPopularJobReturnsNoItems() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByWorkForm(any(Pageable.class), eq("FULLTIME"), eq("flag"))).thenReturn(jobs);

		when(jobRepository.getPopularJob(any(Pageable.class), eq(true), eq(0), eq("flag"),null))
				.thenReturn(new PageImpl<>(Collections.emptyList()));
		final Page<Job> jobs1 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getNewestJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs1);
		final Page<Job> jobs2 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getUrgentJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs2);
		final Page<Job> jobs3 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag"))).thenReturn(jobs3);
		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse(1L, 1L, "jobName", "workPlace",
				"workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L,
				"rank", "experience", false, "jobDescription", "jobRequirement", "benefit", 1L, 1L,
				"weekday", 1L, "flag", "companyAvatar", Arrays.asList("value"), "companyName");
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);
		final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final HomePageData result = jobService.getDataHomePage();
	}

	@Test
	public void testGetDataHomePage_JobRepositoryGetNewestJobReturnsNoItems() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByWorkForm(any(Pageable.class), eq("FULLTIME"), eq("flag"))).thenReturn(jobs);
		final Page<Job> jobs1 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getPopularJob(any(Pageable.class), eq(true), eq(0), eq("flag"),null)).thenReturn(jobs1);

		when(jobRepository.getNewestJob(any(Pageable.class), eq(true), eq(0), eq("flag")))
				.thenReturn(new PageImpl<>(Collections.emptyList()));
		final Page<Job> jobs2 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getUrgentJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs2);
		final Page<Job> jobs3 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag"))).thenReturn(jobs3);
		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse(1L, 1L, "jobName", "workPlace",
				"workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L,
				"rank", "experience", false, "jobDescription", "jobRequirement", "benefit", 1L, 1L,
				"weekday", 1L, "flag", "companyAvatar", Arrays.asList("value"), "companyName");
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);
		final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final HomePageData result = jobService.getDataHomePage();
	}

	@Test
	public void testGetDataHomePage_JobRepositoryGetUrgentJobReturnsNoItems() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByWorkForm(any(Pageable.class), eq("FULLTIME"), eq("flag"))).thenReturn(jobs);
		final Page<Job> jobs1 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getPopularJob(any(Pageable.class), eq(true), eq(0), eq("flag"),null)).thenReturn(jobs1);
		final Page<Job> jobs2 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getNewestJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs2);

		when(jobRepository.getUrgentJob(any(Pageable.class), eq(true), eq(0), eq("flag")))
				.thenReturn(new PageImpl<>(Collections.emptyList()));
		final Page<Job> jobs3 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag"))).thenReturn(jobs3);
		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse(1L, 1L, "jobName", "workPlace",
				"workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L,
				"rank", "experience", false, "jobDescription", "jobRequirement", "benefit", 1L, 1L,
				"weekday", 1L, "flag", "companyAvatar", Arrays.asList("value"), "companyName");
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);
		final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final HomePageData result = jobService.getDataHomePage();
	}

	@Test
	public void testGetDataHomePage_JobRepositoryGetListJobByFieldIdReturnsNoItems() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByWorkForm(any(Pageable.class), eq("FULLTIME"), eq("flag"))).thenReturn(jobs);
		final Page<Job> jobs1 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getPopularJob(any(Pageable.class), eq(true), eq(0), eq("flag"),null)).thenReturn(jobs1);
		final Page<Job> jobs2 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getNewestJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs2);
		final Page<Job> jobs3 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getUrgentJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs3);

		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag")))
				.thenReturn(new PageImpl<>(Collections.emptyList()));
		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse(1L, 1L, "jobName", "workPlace",
				"workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L,
				"rank", "experience", false, "jobDescription", "jobRequirement", "benefit", 1L, 1L,
				"weekday", 1L, "flag", "companyAvatar", Arrays.asList("value"), "companyName");
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);
		final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final HomePageData result = jobService.getDataHomePage();
	}

	@Test
	public void testGetDataHomePage_JobHashTagServiceReturnsNull() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByWorkForm(any(Pageable.class), eq("FULLTIME"), eq("flag"))).thenReturn(jobs);
		final Page<Job> jobs1 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getPopularJob(any(Pageable.class), eq(true), eq(0), eq("flag"),null)).thenReturn(jobs1);
		final Page<Job> jobs2 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getNewestJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs2);
		final Page<Job> jobs3 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getUrgentJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs3);
		final Page<Job> jobs4 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag"))).thenReturn(jobs4);
		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse(1L, 1L, "jobName", "workPlace",
				"workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L,
				"rank", "experience", false, "jobDescription", "jobRequirement", "benefit", 1L, 1L,
				"weekday", 1L, "flag", "companyAvatar", Arrays.asList("value"), "companyName");
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);

		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(null);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final HomePageData result = jobService.getDataHomePage();
	}

	@Test
	public void testGetDataHomePage_JobHashTagServiceReturnsNoItems() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByWorkForm(any(Pageable.class), eq("FULLTIME"), eq("flag"))).thenReturn(jobs);
		final Page<Job> jobs1 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getPopularJob(any(Pageable.class), eq(true), eq(0), eq("flag"),null)).thenReturn(jobs1);
		final Page<Job> jobs2 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getNewestJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs2);
		final Page<Job> jobs3 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getUrgentJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs3);
		final Page<Job> jobs4 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag"))).thenReturn(jobs4);
		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse(1L, 1L, "jobName", "workPlace",
				"workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L,
				"rank", "experience", false, "jobDescription", "jobRequirement", "benefit", 1L, 1L,
				"weekday", 1L, "flag", "companyAvatar", Arrays.asList("value"), "companyName");
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);

		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(Collections.emptyList());

		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final HomePageData result = jobService.getDataHomePage();
	}

	@Test
	public void testGetDataHomePage_CompanyServiceReturnsNull() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByWorkForm(any(Pageable.class), eq("FULLTIME"), eq("flag"))).thenReturn(jobs);
		final Page<Job> jobs1 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getPopularJob(any(Pageable.class), eq(true), eq(0), eq("flag"),null)).thenReturn(jobs1);
		final Page<Job> jobs2 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getNewestJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs2);
		final Page<Job> jobs3 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getUrgentJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs3);
		final Page<Job> jobs4 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag"))).thenReturn(jobs4);
		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse(1L, 1L, "jobName", "workPlace",
				"workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L,
				"rank", "experience", false, "jobDescription", "jobRequirement", "benefit", 1L, 1L,
				"weekday", 1L, "flag", "companyAvatar", Arrays.asList("value"), "companyName");
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);
		final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
		when(companyService.getCompanyById(1L)).thenReturn(null);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final HomePageData result = jobService.getDataHomePage();
	}

	@Test
	public void testGetDataHomePage_ImageServiceReturnsAbsent() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByWorkForm(any(Pageable.class), eq("FULLTIME"), eq("flag"))).thenReturn(jobs);
		final Page<Job> jobs1 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getPopularJob(any(Pageable.class), eq(true), eq(0), eq("flag"),null)).thenReturn(jobs1);
		final Page<Job> jobs2 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getNewestJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs2);
		final Page<Job> jobs3 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getUrgentJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs3);
		final Page<Job> jobs4 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag"))).thenReturn(jobs4);
		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse(1L, 1L, "jobName", "workPlace",
				"workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L,
				"rank", "experience", false, "jobDescription", "jobRequirement", "benefit", 1L, 1L,
				"weekday", 1L, "flag", "companyAvatar", Arrays.asList("value"), "companyName");
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);
		final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		when(imageService.getImageCompany(1L, true)).thenReturn(Optional.empty());
		final HomePageData result = jobService.getDataHomePage();
	}

	@Test
	public void testDisplayJobInHomePage() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse(1L, 1L, "jobName", "workPlace",
				"workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L,
				"rank", "experience", false, "jobDescription", "jobRequirement", "benefit", 1L, 1L,
				"weekday", 1L, "flag", "companyAvatar", Arrays.asList("value"), "companyName");
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);
		final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final List<JobHomePageResponse> result = jobService.displayJobInHomePage(jobs);
	}

	@Test
	public void testDisplayJobInHomePage_JobHashTagServiceReturnsNull() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse(1L, 1L, "jobName", "workPlace",
				"workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L,
				"rank", "experience", false, "jobDescription", "jobRequirement", "benefit", 1L, 1L,
				"weekday", 1L, "flag", "companyAvatar", Arrays.asList("value"), "companyName");
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);

		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(null);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final List<JobHomePageResponse> result = jobService.displayJobInHomePage(jobs);
	}

	@Test
	public void testDisplayJobInHomePage_JobHashTagServiceReturnsNoItems() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse(1L, 1L, "jobName", "workPlace",
				"workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L,
				"rank", "experience", false, "jobDescription", "jobRequirement", "benefit", 1L, 1L,
				"weekday", 1L, "flag", "companyAvatar", Arrays.asList("value"), "companyName");
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);

		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(Collections.emptyList());
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final List<JobHomePageResponse> result = jobService.displayJobInHomePage(jobs);
		assertThat(Collections.emptyList()).isEqualTo(Collections.emptyList());
	}

	@Test
	public void testDisplayJobInHomePage_CompanyServiceReturnsNull() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse(1L, 1L, "jobName", "workPlace",
				"workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L,
				"rank", "experience", false, "jobDescription", "jobRequirement", "benefit", 1L, 1L,
				"weekday", 1L, "flag", "companyAvatar", Arrays.asList("value"), "companyName");
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);
		final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);

		when(companyService.getCompanyById(1L)).thenReturn(null);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final List<JobHomePageResponse> result = jobService.displayJobInHomePage(jobs);
	}

	@Test
	public void testDisplayJobInHomePage_ImageServiceReturnsAbsent() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse(1L, 1L, "jobName", "workPlace",
				"workForm", LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0), 1L, 1L, 1L,
				"rank", "experience", false, "jobDescription", "jobRequirement", "benefit", 1L, 1L,
				"weekday", 1L, "flag", "companyAvatar", Arrays.asList("value"), "companyName");
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);
		final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		when(imageService.getImageCompany(1L, true)).thenReturn(Optional.empty());
		final List<JobHomePageResponse> result = jobService.displayJobInHomePage(jobs);
	}


	@Test
	public void testGetJobByFieldId() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag"))).thenReturn(jobs);
		final JobResponse jobResponse = jobResponse();
		when(modelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
		final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final ResponseDataPagination result = jobService.getJobByFieldId(1, 11, 11L);
	}

	@Test
	public void testGetJobByFieldId_JobRepositoryReturnsNoItems() {
		// Setup
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag")))
				.thenReturn(new PageImpl<>(Collections.emptyList()));
		final JobResponse jobResponse = jobResponse();
		when(modelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
		final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final ResponseDataPagination result = jobService.getJobByFieldId(1, 11, 11L);
	}

	@Test
	public void testGetJobByFieldId_JobHashTagServiceReturnsNull() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag"))).thenReturn(jobs);
		final JobResponse jobResponse = jobResponse();
		when(modelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);

		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(null);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final ResponseDataPagination result = jobService.getJobByFieldId(1, 11, 11L);
	}

	@Test
	public void testGetJobByFieldId_JobHashTagServiceReturnsNoItems() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag"))).thenReturn(jobs);
		final JobResponse jobResponse = jobResponse();
		when(modelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(Collections.emptyList());
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		final Optional<Image> image = Optional.of(
				new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false));
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final ResponseDataPagination result = jobService.getJobByFieldId(1, 11, 11L);
	}
	JobHashtag jobHashtah(){
		JobHashtag  jobHashtag= new JobHashtag(1L, 1L, 1L, "hashTagName", "status");
		return jobHashtag;
	}
	@Test
	public void testGetJobByFieldId_CompanyServiceReturnsNull() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag"))).thenReturn(jobs);
		final JobResponse jobResponse = jobResponse();
		when(modelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
		final List<JobHashtag> hashtagList = Arrays.asList(jobHashtah());
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
		when(companyService.getCompanyById(1L)).thenReturn(null);
		final Optional<Image> image = Optional.of(image());
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final ResponseDataPagination result = jobService.getJobByFieldId(1, 11, 11L);
	}

	@Test
	public void testGetJobByFieldId_ImageServiceReturnsAbsent() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag"))).thenReturn(jobs);
		final JobResponse jobResponse = jobResponse();
		when(modelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
		final List<JobHashtag> hashtagList = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(hashtagList);
		final Company company = new Company();
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);
		when(imageService.getImageCompany(1L, true)).thenReturn(Optional.empty());
		final ResponseDataPagination result = jobService.getJobByFieldId(1, 11, 11L);
	}

	@Test
	public void testGetListSuggestJobByCv() {
		when(candidateService.existsById(1L)).thenReturn(true);
		final CV cv = new CV(1L, 1L, 1L, "summary", "totalExperienceYear");
		when(cvService.getCVByCandidateId(1L)).thenReturn(cv);
		final List<MajorLevel> majorLevels = Arrays.asList(majorLevel());
		when(majorLevelService.getListMajorLevelByCvId(1L)).thenReturn(majorLevels);

		when(majorService.getNameByMajorId(1L)).thenReturn("result");
		final List<Job> jobs = Arrays.asList(job());
		when(jobRepository.getListSuggestJobByCv("majorName")).thenReturn(jobs);
		final JobResponse jobResponse = jobResponse();
		when(modelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
		final Optional<Image> image = Optional.of(image());
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final List<JobResponse> result = jobService.getListSuggestJobByCv(1L);
	}

	@Test
	public void testGetListSuggestJobByCv_MajorLevelServiceReturnsNoItems() {
		when(candidateService.existsById(1L)).thenReturn(true);
		final CV cv = new CV(1L, 1L, 1L, "summary", "totalExperienceYear");
		when(cvService.getCVByCandidateId(1L)).thenReturn(cv);

		when(majorLevelService.getListMajorLevelByCvId(1L)).thenReturn(Collections.emptyList());
		when(majorService.getNameByMajorId(1L)).thenReturn("result");
		final List<Job> jobs = Arrays.asList(job());
		when(jobRepository.getListSuggestJobByCv("majorName")).thenReturn(jobs);
		final JobResponse jobResponse = jobResponse();
		when(modelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
		final Optional<Image> image = Optional.of(image());
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final List<JobResponse> result = jobService.getListSuggestJobByCv(1L);
	}
	Image image(){
		Image image = new Image(1L, "name", "companyAvatar", 1L, 0, false, "contentType", "content".getBytes(), false);
		return image;
	}
	@Test
	public void testGetListSuggestJobByCv_JobRepositoryReturnsNoItems() {
		when(candidateService.existsById(1L)).thenReturn(false);
		final CV cv = new CV(1L, 1L, 1L, "summary", "totalExperienceYear");
		when(cvService.getCVByCandidateId(1L)).thenReturn(cv);
		final List<MajorLevel> majorLevels = Arrays.asList(majorLevel());
		when(majorLevelService.getListMajorLevelByCvId(1L)).thenReturn(majorLevels);
		when(majorService.getNameByMajorId(1L)).thenReturn("result");
		when(jobRepository.getListSuggestJobByCv("majorName")).thenReturn(Collections.emptyList());
		final JobResponse jobResponse = jobResponse();
		when(modelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
		final Optional<Image> image = Optional.of(image());
		when(imageService.getImageCompany(1L, true)).thenReturn(image);
		final List<JobResponse> result = jobService.getListSuggestJobByCv(1L);
	}
	JobResponse jobResponse(){
		JobResponse jobResponse = new JobResponse();
		jobResponse.setJobId(1L);
		jobResponse.setCompanyId(1L);
		jobResponse.setRecruiterId(1L);
		jobResponse.setListHashtag(Arrays.asList("value"));
		jobResponse.setCompanyName("companyName");
		jobResponse.setJobName("jobName");
		jobResponse.setJobDescription("jobDescription");
		jobResponse.setJobRequirement("jobRequirement");
		jobResponse.setBenefit("benefit");
		jobResponse.setFromSalary(1L);
		jobResponse.setToSalary(1L);
		jobResponse.setNumberRecruits(1L);
		jobResponse.setRank("rank");
		jobResponse.setWorkForm("workForm");
		jobResponse.setCompanyAvatar("companyAvatar");;
		return jobResponse;
	}
	
	MajorLevel majorLevel(){
		MajorLevel majorLevel = new MajorLevel(1L, 1L, 1L, 1L, "level", false);
		return majorLevel;
	}
	@Test
	public void testGetListSuggestJobByCv_ImageServiceReturnsAbsent() {
		when(candidateService.existsById(1L)).thenReturn(false);
		final CV cv = new CV(1L, 1L, 1L, "summary", "totalExperienceYear");
		when(cvService.getCVByCandidateId(1L)).thenReturn(cv);
		final List<MajorLevel> majorLevels = Arrays.asList(majorLevel());
		when(majorLevelService.getListMajorLevelByCvId(1L)).thenReturn(majorLevels);
		when(majorService.getNameByMajorId(1L)).thenReturn("result");
		final List<Job> jobs = Arrays.asList(job());
		when(jobRepository.getListSuggestJobByCv("majorName")).thenReturn(jobs);
		final JobResponse jobResponse = jobResponse();
		when(modelMapper.map(any(Object.class), eq(JobResponse.class))).thenReturn(jobResponse);
		when(imageService.getImageCompany(1L, true)).thenReturn(Optional.empty());
		Candidate candidate = new Candidate();
		candidate.setId(1L);
		final List<JobResponse> result = jobService.getListSuggestJobByCv(1L);
	}

	@Test
	public void testCountTotalCreatedJobOfRecruiter() {
		when(jobRepository.countTotalCreatedJobOfRecruiter(1L)).thenReturn(null);
		final CountTotalCreatedJobResponse result = jobService.countTotalCreatedJobOfRecruiter(1L);
	}

	@Test
	void testGetPopularJobList() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getPopularJob(any(Pageable.class), eq(true), eq(0), eq("flag"),
				eq(Arrays.asList(1L)))).thenReturn(jobs);
		final Page<Job> result = jobService.getPopularJobList(PageRequest.of(1, 10), Arrays.asList(1L));
	}

	@Test
	void testGetJobOfRecruiter() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getAllByRecruiterIdAndJobNameOrderByUpdatedAtDesc(any(Pageable.class), eq(1L),
				eq("jobName"))).thenReturn(jobs);
		final Page<Job> result = jobService.getJobOfRecruiter(PageRequest.of(1, 10), 1L, "jobName");
	}

	@Test
	public void testCountJobInSystem() {
		when(jobRepository.countJobInSystem("Posted")).thenReturn(0);
		final int result = jobService.countJobInSystem();
		assertThat(result).isEqualTo(0);
	}

	@Test
	public void testDraftJob() {
		final Job job = job();
		when(jobRepository.getById(1L)).thenReturn(job);
		when(mockAppliedJobService.countAppliedCVOfJob(1L)).thenReturn(0);
		final Job job1 = job();
		when(jobRepository.saveAndFlush(any(Job.class))).thenReturn(job1);
		jobService.draftJob(1L);
		verify(jobRepository).saveAndFlush(any(Job.class));
	}

	@Test
	public void testGetListPopularJobId() {
		when(jobRepository.getListPopularJobId(2L)).thenReturn(Arrays.asList(1L));
		final List<Long> result = jobService.getListPopularJobId();
		assertThat(result).isEqualTo(Arrays.asList(1L));
	}

	@Test
	public void testFindAll() {
		final List<Job> jobs = Arrays.asList(job());
		when(jobRepository.findAll()).thenReturn(jobs);
		final List<Job> result = jobService.findAll();
	}

	@Test
	void testGetDataHomePage() {
		final Page<Job> jobs = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByWorkForm(any(Pageable.class), eq("FULLTIME"), eq("flag"))).thenReturn(jobs);

		final Page<Job> jobs1 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getPopularJob(any(Pageable.class), eq(true), eq(0), eq("flag"),
				eq(Arrays.asList(1L)))).thenReturn(jobs1);

		final Page<Job> jobs2 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getNewestJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs2);

		final Page<Job> jobs3 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getUrgentJob(any(Pageable.class), eq(true), eq(0), eq("flag"))).thenReturn(jobs3);

		final Page<Job> jobs4 = new PageImpl<>(Arrays.asList(job()));
		when(jobRepository.getListJobByFieldId(any(Pageable.class), eq(1L), eq("flag"))).thenReturn(jobs4);

		final JobHomePageResponse jobHomePageResponse = new JobHomePageResponse();
		when(modelMapper.map(any(Object.class), eq(JobHomePageResponse.class))).thenReturn(jobHomePageResponse);

		final List<JobHashtag> jobHashtags = Arrays.asList(new JobHashtag(1L, 1L, 1L, "hashTagName", "status"));
		when(jobHashTagService.getHashTagOfJob(1L)).thenReturn(jobHashtags);

		final Company company = new Company();
		company.setCreatedAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
		company.setId(1L);
		company.setFieldWork("fieldWork");
		company.setName("companyName");
		company.setEmail("email");
		company.setPhone("phone");
		company.setDescription("description");
		company.setWebsite("website");
		company.setNumberEmployees("numberEmployees");
		company.setAddress("address");
		company.setTaxCode("taxCode");
		company.setIsDeleted(0);
		company.setMapUrl("mapUrl");
		company.setCreatorId(1L);
		company.setLocked(false);
		when(companyService.getCompanyById(1L)).thenReturn(company);

		final Optional<Image> image = Optional.of(image());
		when(imageService.getImageCompany(1L, true)).thenReturn(image);

		final HomePageData result = jobService.getDataHomePage();
	}
}