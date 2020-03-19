# event-manager
Event manager that I use in my projects


# inclusion

### gradle
* Add it in your root build.gradle at the end of repositories:
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
* Add the dependency
```
dependencies {
		implementation 'com.github.Mat1337:event-manager:1.0'
}
```

### maven
* Add the JitPack repository to your build file
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
* Add the dependency
```
<dependency>
	   <groupId>com.github.Mat1337</groupId>
	   <artifactId>event-manager</artifactId>
	   <version>1.1</version>
</dependency>
```
