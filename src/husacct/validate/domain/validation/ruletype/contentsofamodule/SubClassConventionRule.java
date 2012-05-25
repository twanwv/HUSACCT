package husacct.validate.domain.validation.ruletype.contentsofamodule;

import husacct.common.dto.DependencyDTO;
import husacct.common.dto.RuleDTO;
import husacct.validate.domain.check.util.CheckConformanceUtilClass;
import husacct.validate.domain.configuration.ConfigurationServiceImpl;
import husacct.validate.domain.factory.violationtype.ViolationTypeFactory;
import husacct.validate.domain.validation.Severity;
import husacct.validate.domain.validation.Violation;
import husacct.validate.domain.validation.ViolationType;
import husacct.validate.domain.validation.iternal_tranfer_objects.Mapping;
import husacct.validate.domain.validation.ruletype.RuleType;
import husacct.validate.domain.validation.ruletype.RuleTypes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SubClassConventionRule extends RuleType {

	private final static EnumSet<RuleTypes> exceptionrules = EnumSet.of(RuleTypes.IS_ALLOWED);

	public SubClassConventionRule(String key, String category, List<ViolationType> violationtypes, Severity severity) {
		super(key, category, violationtypes, exceptionrules, severity);
	}

	@Override
	public List<Violation> check(ConfigurationServiceImpl configuration, RuleDTO rootRule, RuleDTO currentRule) {
		this.violations = new ArrayList<Violation>();
		this.violationtypefactory = new ViolationTypeFactory().getViolationTypeFactory(configuration);

		this.mappings = CheckConformanceUtilClass.filterClasses(currentRule);
		List<Mapping> physicalClasspathsFrom = mappings.getMappingFrom();
		List<Mapping> physicalClasspathsTo = mappings.getMappingTo();

		int counter = 0, noDependencyCounter = 0;
		for(Mapping classPathFrom : physicalClasspathsFrom){			
			for(Mapping classPathTo : physicalClasspathsTo){
				DependencyDTO[] dependencies = analyseService.getDependencies(classPathFrom.getPhysicalPath(), classPathTo.getPhysicalPath(), classPathFrom.getViolationTypes());
				counter++;
				if(dependencies.length == 0) noDependencyCounter++;			
			}
			if(noDependencyCounter == counter){
				Violation violation = createViolation(rootRule, classPathFrom, null, null, configuration);
				violations.add(violation);
			}
		}	
		return violations;
	}
}
