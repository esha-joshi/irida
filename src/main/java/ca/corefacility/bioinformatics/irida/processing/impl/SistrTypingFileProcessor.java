package ca.corefacility.bioinformatics.irida.processing.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission.Builder;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.Sets;

/**
 * File processor which launches a SISTR typing pipeline
 * on uploaded sequences. It will check with a sequence's associated project for
 * whether or not it should type with SISTR.
 */
@Component
public class SistrTypingFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(SistrTypingFileProcessor.class);

	private final SequencingObjectRepository objectRepository;
	private final SampleSequencingObjectJoinRepository ssoRepository;
	private final ProjectSampleJoinRepository psjRepository;
	private final AnalysisSubmissionRepository submissionRepository;
	private final UserRepository userRepository;
	private final IridaWorkflowsService workflowsService;

	@Autowired
	public SistrTypingFileProcessor(SequencingObjectRepository objectRepository,
			AnalysisSubmissionRepository submissionRepository, IridaWorkflowsService workflowsService,
			UserRepository userRepository, SampleSequencingObjectJoinRepository ssoRepository,
			ProjectSampleJoinRepository psjRepository) {
		this.objectRepository = objectRepository;
		this.submissionRepository = submissionRepository;
		this.workflowsService = workflowsService;
		this.userRepository = userRepository;
		this.ssoRepository = ssoRepository;
		this.psjRepository = psjRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void process(Long sequenceFileId) throws FileProcessorException {
		SequencingObject sequencingObject = objectRepository.findOne(sequenceFileId);

		logger.debug("Setting up SISTR typing for sequence " + sequencingObject.getId());

		User admin = userRepository.loadUserByUsername("admin");

		// Ensure it's a paired upload. Single end can't currently be
		// assembled/typed.
		if (sequencingObject instanceof SequenceFilePair) {
			IridaWorkflow defaultWorkflowByType;

			// get the workflow
			try {
				defaultWorkflowByType = workflowsService.getDefaultWorkflowByType(AnalysisType.SISTR_TYPING);
			} catch (IridaWorkflowNotFoundException e) {
				throw new FileProcessorException("Cannot find assembly workflow", e);
			}

			UUID pipelineUUID = defaultWorkflowByType.getWorkflowIdentifier();

			// build an AnalysisSubmission
			Builder builder = new AnalysisSubmission.Builder(pipelineUUID);
			AnalysisSubmission submission = builder
					.inputFilesPaired(Sets.newHashSet((SequenceFilePair) sequencingObject))
					.name("Automated SISTR Typing " + sequencingObject.toString()).build();
			submission.setSubmitter(admin);

			submission = submissionRepository.save(submission);

			// Associate the submission with the seqobject
			sequencingObject.setSistrTyping(submission);

			objectRepository.save(sequencingObject);

			logger.debug("Automated SISTR typing submission created for sequencing object " + sequencingObject.getId());
		} else {
			logger.warn("Could not run SISTR typing for sequencing object " + sequencingObject.getId()
					+ " because it's not paired end");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean modifiesFile() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean shouldProcessFile(Long sequencingObjectId) {
		SequencingObject sequencingObject = objectRepository.findOne(sequencingObjectId);

		return shouldTypeWithSISTR(sequencingObject);
	}

	/**
	 * Check whether any {@link Project} associated with the
	 * {@link SequencingObject} is set to type with SISTR.
	 * 
	 * @param object
	 *            {@link SequencingObject} to check to type with SISTR.
	 * @return true if it should type with SISTR, false otherwise
	 */
	private boolean shouldTypeWithSISTR(SequencingObject object) {
		boolean type = false;

		SampleSequencingObjectJoin sampleForSequencingObject = ssoRepository.getSampleForSequencingObject(object);

		/*
		 * This is something that should only ever happen in tests, but added
		 * check with a warning
		 */
		if (sampleForSequencingObject != null) {
			List<Join<Project, Sample>> projectForSample = psjRepository
					.getProjectForSample(sampleForSequencingObject.getSubject());

			type = projectForSample.stream().anyMatch(j -> j.getSubject().getSistrTypingUploads());
		} else {
			logger.warn("Cannot find sample for sequencing object.  Not typing with SISTR");
		}

		return type;
	}

}