package com.kazurayam.webdriverfactory.firefox

import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.WebDriverFactoryException
import com.kazurayam.webdriverfactory.utils.PathUtils
import org.openqa.selenium.InvalidArgumentException

import java.nio.file.Files
import java.nio.file.Path

import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration
import java.util.concurrent.TimeUnit

class FirefoxDriverFactoryImpl extends FirefoxDriverFactory {

	static Logger logger_ = LoggerFactory.getLogger(FirefoxDriverFactoryImpl.class)

	private final Set<FirefoxPreferencesModifier> firefoxPreferencesModifiers
	private final Set<FirefoxOptionsModifier> firefoxOptionsModifiers

	private Integer pageLoadTimeoutSeconds

	FirefoxDriverFactoryImpl() {
		this(true)
	}

	FirefoxDriverFactoryImpl(boolean requireDefaultSettings) {
		this.firefoxPreferencesModifiers  = new HashSet<>()
		this.firefoxOptionsModifiers      = new HashSet<>()
		if (requireDefaultSettings) {
			this.prepareDefaultSettings()
		}
		pageLoadTimeoutSeconds = 60
	}

	private void prepareDefaultSettings() {
		this.addFirefoxPreferencesModifier(FirefoxPreferencesModifiers.downloadWithoutPrompt())
		this.addFirefoxPreferencesModifier(FirefoxPreferencesModifiers.downloadIntoUserHomeDownloadsDirectory())

		this.addFirefoxOptionsModifier(FirefoxOptionsModifier.windowSize1024x768)
	}

	@Override
	void addFirefoxPreferencesModifier(FirefoxPreferencesModifier fpm) {
		if (this.firefoxPreferencesModifiers.contains(fpm)) {
			// The late comer wins
			this.firefoxPreferencesModifiers.remove(fpm)
		}
		firefoxPreferencesModifiers.add(fpm)
	}

	@Override
	void addAllFirefoxPreferencesModifier(List<FirefoxPreferencesModifier> list) {
		list.each({ FirefoxPreferencesModifier fpm ->
			this.firefoxPreferencesModifiers.add(fpm)
		})
	}

	@Override
	void addFirefoxOptionsModifier(FirefoxOptionsModifier fom) {
		if (this.firefoxOptionsModifiers.contains(fom)) {
			// The late comer wins
			this.firefoxOptionsModifiers.remove(fom)
		}
		firefoxPreferencesModifiers.add(fom)
	}

	@Override
	void addAllFirefoxOptionsModifier(List<FirefoxOptionsModifier> list) {
		list.each({ FirefoxOptionsModifier fom ->
			this.firefoxOptionsModifiers.add(fom)
		})
	}

	@Override
	void pageLoadTimeout(Integer waitSeconds) {
		Objects.requireNonNull(waitSeconds)
		if (waitSeconds <= 0) {
			throw new IllegalArgumentException("waitSeconds=${waitSeconds} must not be <=0")
		}
		if (waitSeconds > 999) {
			throw new IllegalArgumentException("waitSeconds=${waitSeconds} must not be > 999")
		}
		this.pageLoadTimeoutSeconds = waitSeconds
	}

	static protected void setPageLoadTimeout(FirefoxDriver driver, Integer seconds) {
		if (seconds != Integer.MIN_VALUE) {
			Duration dur = Duration.ofSeconds((long)seconds)
			long millis = dur.toMillis()
			driver.manage().timeouts().pageLoadTimeout(millis, TimeUnit.MILLISECONDS);
		}
	}




	@Override
	LaunchedFirefoxDriver newFirefoxDriver() {
		FirefoxOptions options = buildOptions(
				this.firefoxPreferencesModifiers,
				this.firefoxOptionsModifiers
		)
		FirefoxDriver driver = new FirefoxDriver(options)
		setPageLoadTimeout(driver, this.pageLoadTimeoutSeconds)
		LaunchedFirefoxDriver launched =
				new LaunchedFirefoxDriver(driver).setEmployedOptions(options)
		return launched
	}

	@Override
	LaunchedFirefoxDriver newFirefoxDriver(UserProfile userProfile) {
		return newFirefoxDriver(userProfile, UserDataAccess.TO_GO)
	}

	@Override
	LaunchedFirefoxDriver newFirefoxDriver(UserProfile userProfile, UserDataAccess instruction) {
		Objects.requireNonNull(userProfile, "userProfile must not be null")
		Objects.requireNonNull(instruction, "instruction must not be null")
		FirefoxUserProfile firefoxUserProfile = FirefoxProfileUtils.findFirefoxUserProfile(userProfile)
		if (firefoxUserProfile == null) {
			throw new WebDriverFactoryException(
					"FirefoxUserProfile of \"${userProfile}\" is not found in :" +
					"\n" + FirefoxProfileUtils.allFirefoxUserProfilesAsString())
		}
		Path userDataDir = FirefoxProfileUtils.getDefaultUserDataDir()
		ProfileDirectoryName profileDirectoryName = firefoxUserProfile.getProfileDirectoryName()
		return launchFirefox(userDataDir, profileDirectoryName, instruction)
	}

	@Override
	LaunchedFirefoxDriver newFirefoxDriver(ProfileDirectoryName profileDirectoryName) {
		return this.newChromeDriver(profileDirectoryName, UserDataAccess.TO_GO)
	}

	@Override
	LaunchedFirefoxDriver newFirefoxDriver(ProfileDirectoryName profileDirectoryName, UserDataAccess instruction) {
		Objects.requireNonNull(profileDirectoryName, "profileDirectoryName must not be null")
		Objects.requireNonNull(instruction, "instruction must not be null")
		Path userDataDir = FirefoxProfileUtils.getDefaultUserDataDir()
		return launchFirefox(userDataDir, profileDirectoryName)
	}

	@Override
	void enableFirefoxDriverLog(Path outputDirectory) {
		Objects.requireNonNull(outputDirectory)
		if (!Files.exists(outputDirectory)) {
			Files.createDirectories(outputDirectory)
		}
		FirefoxDriverUtils.enableFirefoxDriverLog(outputDirectory)
	}

	/**
	 * Launch a Firefox browser.
	 */
	private LaunchedFirefoxDriver launchFirefox(
			Path userDataDir,
			ProfileDirectoryName profileDirectoryName,
			UserDataAccess instruction
	) {
		Objects.requireNonNull(userDataDir)
		Objects.requireNonNull(profileDirectoryName)
		Objects.requireNonNull(instruction)
		if (! Files.exists(userDataDir)) {
			throw new IllegalArgumentException("${userDataDir} is not present")
		}
		Path sourceProfileDirectory = userDataDir.resolve(profileDirectoryName.toString())
		assert Files.exists(sourceProfileDirectory)
		Path targetUserDataDir = userDataDir
		if (instruction == FirefoxDriverFactory.UserDataAccess.TO_GO) {
			targetUserDataDir = Files.createTempDirectory("__user-data-dir__")
			Path targetProfileDirectory = targetUserDataDir.resolve(profileDirectoryName.getName())
			PathUtils.copyDirectoryRecursively(
					sourceProfileDirectory,
					targetProfileDirectory)
			int numCopied = PathUtils.listDirectoryRecursively(targetProfileDirectory).size()
			logger_.info("copied ${numCopied} files from ${sourceProfileDirectory} into ${targetProfileDirectory} ")
		} else {
			logger_.debug("will use ${sourceProfileDirectory} ")
		}

		// use the specified UserProfile with which Firefox browser is launched
		FirefoxOptionsModifier fom =
				FirefoxOptionsModifiers.withUserProfile(
						targetUserDataDir,
						profileDirectoryName.getName())
		this.addFirefoxOptionsModifier(fom)

		// launch the Firefix driver
		FirefoxDriver driver = null
		try {
			FirefoxOptions options = buildOptions(
					this.firefoxPreferencesModifiers,
					this.firefoxOptionsModifiers
			)
			driver = new FirefoxDriver(options)
			setPageLoadTimeout(driver, this.pageLoadTimeoutSeconds)
			FirefoxUserProfile fup = new FirefoxUserProfile(targetUserDataDir, profileDirectoryName)
			LaunchedFirefoxDriver launched = new LaunchedFirefoxDriver(driver)
					.setFirefoxUserProfile(fup)
					.setInstruction(instruction).setEmployedOptions(options)
			return launched
		} catch (InvalidArgumentException iae) {
			if (driver != null) {
				driver.quit()
				logger_.info("forcibly closed the browser")
			}
			StringBuilder sb = new StringBuilder()
			sb.append("targetUserDataDir=\"${targetUserDataDir}\"\n")
			sb.append("profileDirectoryName=\"${profileDirectoryName}\"\n")
			sb.append("org.openqa.selenium.InvalidArgumentException was thrown.\n")
			sb.append("Exception message:\n\n")
			sb.append(iae.getMessage())
			throw new WebDriverFactoryException(sb.toString())
		}
	}

	private FirefoxOptions buildOptions(
			Set<FirefoxPreferencesModifier> firefoxPreferencesModifiers,
			Set<FirefoxOptionsModifier> firefoxOptionsModifiers
	) {
		Map<String, Object> preferences = new HashMap<>()

		preferences = applyFirefoxPreferencesModifiers(preferences,
				firefoxPreferencesModifiers)

		FirefoxOptions firefoxOptions =
				FirefoxOptionsBuilder.newInstance(preferences).build()

		firefoxOptions = applyFirefoxOptionsModifiers(firefoxOptions,
				firefoxPreferencesModifiers)

		return firefoxOptions
	}

	static Map<String, Object> applyFirefoxPreferencesModifiers(
			Map<String, Object> firefoxPreferences,
			Set<FirefoxPreferencesModifier> modifiers) {
		Map<String, Object> fp = firefoxPreferences
		for (FirefoxPreferencesModifier fpm in modifiers) {
			fp = fpm.modify(fp)
		}
		return fp
	}

	static FirefoxOptions applyFirefoxOptionsModifiers(
			FirefoxOptions firefoxOptions,
			Set<FirefoxOptionsModifier> modifiers) {
		FirefoxOptions fp = firefoxOptions
		for (FirefoxOptionsModifier fom in modifiers) {
			fp = fom.modify(fp)
		}
		return fp
	}
}
