/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.app.util.exporter;

import static org.junit.Assert.*;

import org.junit.*;

import java.io.File;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;

import ghidra.program.model.listing.Program;
import ghidra.program.model.lang.*;
import ghidra.program.util.DefaultLanguageService;
import ghidra.util.task.TaskMonitor;
import ghidra.framework.Application;
import ghidra.base.project.GhidraProject;
import ghidra.app.plugin.core.analysis.AutoAnalysisManager;
import ghidra.app.util.exporter.CppExporter;
import ghidra.test.TestEnv;
import ghidra.framework.Application;
import utility.application.ApplicationLayout;
import utility.application.DummyApplicationLayout;
import ghidra.framework.ApplicationConfiguration;
import ghidra.framework.options.Options;
import ghidra.test.AbstractGhidraHeadlessIntegrationTest;

public class PLTExcludeTest extends AbstractGhidraHeadlessIntegrationTest {
	private Exporter exporter = new CppExporter(null, false, true, false, false, null);
	private int transactionID;
	private Program program;
	private GhidraProject testProject;
	private File testBinary = new File("../../Test/TestResources/src/cpp/simpleHelloWorld/simple_hello_world");
	private File testFile = new File("testFile.c");
	private TestEnv env;

	@Before
	public void setUp() throws Exception {
		ApplicationLayout dummyLayout = new DummyApplicationLayout("TestLayout");
		ApplicationConfiguration configuration = new ApplicationConfiguration();
		if (!Application.isInitialized()) {
			Application.initializeApplication(dummyLayout, configuration);
		}
		env = new TestEnv();
		testProject = env.getGhidraProject();
		Language language = getLanguage("x86:LE:32:default");
		program = testProject.importProgram(testBinary, language, language.getDefaultCompilerSpec());
		transactionID = program.startTransaction("Test");
		AutoAnalysisManager analysisManager = AutoAnalysisManager.getAnalysisManager(program);
		analysisManager.reAnalyzeAll(program.getMemory().getLoadedAndInitializedAddressSet());
		analysisManager.startAnalysis(TaskMonitor.DUMMY, false);
	}

	@After
	public void tearDown() throws Exception {
		if (program != null) {
			program.endTransaction(transactionID, true);
		}
		testFile.delete();
		testProject.close();
	}

	private String[] PLTFunctionNames = { "void FUN_00101040", "void FUN_00101050" };

	@Test
	public void testExcludePLT() throws Exception {
		assertTrue(exporter.export(testFile, program, null, TaskMonitor.DUMMY));
		List<String> lines = Files.readAllLines(Paths.get("testFile.c"));
		for (String line : lines) {
			for (String PLTFunctionName : PLTFunctionNames) {
				assertFalse(line.contains(PLTFunctionName));
			}
		}
	}

	protected void setAnalysisOptions(String optionName) {
		int txId = program.startTransaction("Analyze");
		Options analysisOptions = program.getOptions(Program.ANALYSIS_PROPERTIES);
		analysisOptions.setBoolean(optionName, false);
		program.endTransaction(txId, true);
	}

	private Language getLanguage(String languageName) throws LanguageNotFoundException {
		LanguageService languageService = DefaultLanguageService.getLanguageService();
		return languageService.getLanguage(new LanguageID(languageName));
	}
}