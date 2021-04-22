<!-- (c) https://github.com/MontiCore/monticore -->

This documentation is intended for  **modelers** who use the use case diagram (UCD) language.
A detailed documentation for **language engineers** using or extending the UCD language is 
located **[here](src/main/grammars/UCD.md)**.
We recommend that **language engineers** read this documentation before reading the detailed
documentation.

[[TOC]]

# Example Models

<img width="700" src="doc/pics/UCDOverviewExample.png" alt="The graphical syntax of an example UCD" style="float: left; margin-right: 10px;">
<br><b>Figure 1:</b> The graphical syntax of an example UCD.

&nbsp;  

Figure 1 depicts the UCD ```Example``` in graphical syntax. 
It contains all syntactic UCD elements supported by this MontiCore language.
In textual syntax, the UCD is defined as follows:

``` 
sequencediagram Bid {

  kupfer912:Auction;
  bidPol:BiddingPolicy;
  timePol:TimingPolicy;
  theo:Person;

  kupfer912 -> bidPol : validateBid(bid) {
    bidPol -> kupfer912 : return BiddingPolicy.OK;
  }
  kupfer912 -> timePol : newCurrentClosingTime(kupfer912,bid) {
    timePol -> kupfer912 : return t;
  }
  assert t.timeSec == bid.time.timeSec + extensionTime;
  let int m = theo.messages.size;
  kupfer912 -> theo : sendMessage(bm) {
    theo -> kupfer912 : return;
  }
  assert m + 1 == theo.messages.size;
}
```

# Grammar 
For a detailed description of the syntax of the language please consider reading the comments in 
the corresponding [UCD grammar](src/main/grammars/UCD.mc4).

# Command Line Interface (CLI)

This section describes the CLI tool of the UCD language. 
The CLI tool provides typical functionality used when
processing models. To this effect, it provides funcionality
for 
* parsing, 
* pretty-printing, 
* creating symbol tables, 
* storing symbols in symbol files, 
* loading symbols from symbol files, and 
* semantic differencing. 

The requirements for building and using the UCD CLI tool are that Java 8, Git, and Gradle are 
installed and available for use in Bash. 

The following subsection describes how to download the CLI tool.
Then, this document describes how to build the CLI tool from the source files.
Afterwards, this document contains a tutorial for using the CLI tool.  

## Downloading the Latest Version of the CLI Tool
A ready to use version of the CLI tool can be downloaded in the form of an executable JAR file.
You can use [**this download link**](http://monticore.de/download/UCDCLI.jar) 
for downloading the CLI tool. 

Alternatively, you can download the CLI tool using `wget`.
The following command downloads the latest version of the CLI tool and saves it under the name `UCDCLI` 
in your working directory:
```
wget "http://monticore.de/download/UCDCLI.jar" -O UCDCLI.jar
``` 

## Building the CLI Tool from the Sources
 
It is possible to build an executable JAR of the CLI tool from the source files located in GitHub.
The following describes the process for building the CLI tool from the source files using Bash.
For building an executable Jar of the CLI with Bash from the source files available
in GitHub, execute the following commands.

First, clone the repository:
```
git clone https://github.com/MontiCore/ucd.git
```
Change the directory to the root directory of the cloned sources:
```
cd ucd
```
Afterwards, build the source files with gradle:
```
./gradle build
```
Congratulations! You can now find the executable JAR file `UCDCLI.jar` in
 the directory `target/libs` (accessible via `cd target/libs`).

## Tutorial: Getting Started Using the UCD CLI Tool
The previous sections describe how to obtain an executable JAR file
(UCD CLI tool). This section provides a tutorial for
using the SD CLI tool. The following examples assume
that you locally named the CLI tool `UCDCLI`.

### First Steps
Executing the Jar file without any options prints usage information of the CLI tool to the console:
```
java -jar UCDCLI.jar
TODO
```
To work properly, the CLI tool needs the mandatory argument `-i,--input <arg>`, which takes the file paths of at least one input file containing UCD models.
If no other arguments are specified, the CLI tool solely parses the model(s).

For trying this out, copy the `UCDCLI.jar` into a directory of your choice. 
Afterwards, create a text file containing the following simple UCD:
```
usecasediagram Example {
}
```

Save the text file as `Example.ucd` in the directory where `UCDCLI.jar` is located. 

Now execute the following command:
```
java -jar UCDCLI.jar -i Example.ucd
```

You may notice that the CLI tool prints no output to the console.
This means that the tool has parsed the file `Example.ucd` successfully.

### Step 2: Pretty-Printing
The CLI tool provides a pretty-printer for the UCD language.
A pretty-printer can be used, e.g., to fix the formatting of files containing SDs.
To execute the pretty-printer, the `-pp,--prettyprint` option can be used.
Using the option without any arguments pretty-prints the models contained in the input files to the console.

Execute the following command for trying this out:
```
java -jar UCDCLI.jar -i Example.ucd -pp
```
The command prints the pretty-printed model contained in the input file to the console:
```
usecasediagram Example {
}
```

It is possible to pretty-print the models contained in the input files to output files.
For this task, it is possible to provide the names of output files as arguments to the `-pp,--prettyprint` option.
If arguments for output files are provided, then the number of output files must be equal to the number of input files.
The i-th input file is pretty-printed into the i-th output file.

Execute the following command for trying this out:
```
java -jar UCDCLI.jar -i Example.ucd -pp PPExample.ucd
```
The command prints the pretty-printed model contained in the input file into the file `PPExample.ucd`.

### Step 3: Storing Symbols
Now, we will use the CLI tool to store a symbol file for our `Bid.sd` model.
The stored symbol file will contain information about the objects defined in the SD.
It can be imported by other models for using the symbols introduced by these object definitions,
similar to how we changed the file `Bid.sd` for importing the symbols contained in the
symbol file `Types.typessym`.

Using the `-s,-symboltable <arg>` option builds the symbol tables of the input models and stores them in the file paths given as arguments.
Either no file paths must be provided or exactly one file path has to be provided for each input model.
The symbol file for the i-th input model is stored in the file defined by the i-th file path. 
If you do not provide any file paths, the CLI tool stores the symbol table of each input model 
in the symbol file `target/symbols/{packageName}/{fileName}.sdsym` 
where `packageName` is the name of the package as specified in the file containing 
the model and `fileName` is the name of the file containing the model. The file is stored relative 
to the working directory, i.e., the directory in which you execute the command for storing the symbol files.
Furthermore, please notice that in order to store the symbols properly, the model has to be well-formed in all regards, and therefore all context conditions are checked beforehand.

For storing the symbol file of `Bid.sd`, execute the following command 
(the implicit context condition checks require using the model path option):
```
java -jar SD4DevelopmentCLI.jar -i Bid.sd -path mytypes -s
```
The CLI tool produces the file `target/symbols/Bid.sdsym`, which can now be imported by other models, e.g., by models that need to
use some of the objects defined in the SD `Bid`.

For storing the symbol file of `Bid.sd` in the file `syms/BidSyms.sdsym`, for example, execute the following command
(again, the implicit context condition checks require using the model path option):
```
java -jar SD4DevelopmentCLI.jar -i Bid.sd -path mytypes -s syms/BidSyms.sdsym
```

Congratulations, you have just finished the tutorial about saving SD symbol files!

### Step 4: Semantic Differencing

Semantic differencing of SDs enables developers to detect differences in the meanings of the UCDs.
The semantic difference from an UCD `ucd1` to an UCD `ucd2` is defined as the set of all scenarios 
that are valid in `ucd1` and not valid in `ucd2`. A scenario consists of a variable assignment, use cases,
actors and a relation relating the actors to use cases contained in the scenario. 
A scenario represents that the use cases are executed by the actors according to the relation under 
the circumstances defined by the variable assignment. The semantics of an UCD is the set of scenarios 
explicitly described by the UCD.
 
In this section, we consider the UCDs [rob1.sd](src/test/resources/sddiff/rob1.sd) and [rob2.sd](src/test/resources/sddiff/rob2.sd).
Download the files containing the SDs (by using the links) and place them in the directory where the CLI tool `UCDCLI.jar`
is located. The file located [here](src/test/resources/sddiff/rob1.sd) should be named `SwimmyFish1.sd` and the file 
located [here](src/test/resources/sddiff/rob2.sd) should be named `SwimmyFish2.sd`.

To calculate an element contained in the semantic difference from an UCD to another UCD, 
the CLI tool provides the `-sd,--semdiff` option.
For example, to calculate an element contained in the semantic difference from
the UCD defined in the file `SwimmyFish1.ucd` to the UCD defined in the file `SwimmyFish2.ucd`, execute 
the following command:

```
java -jar UCDCLI.jar -sd -i SwimmyFish1.ucd SwimmyFish2.ucd
```

The CLI prints the following output, which represents the interaction sequence of a system
run that is valid in the SD `rob2` and not valid in the SD `rob1`:
```
Diff witness:
ui -> controller : deliver(r4222,wd40),
controller -> planner : getDeliverPlan(r4222,wd40),
planner -> controller : plan,
planner -> stateProvider : getState(),
controller -> actionExecutor : moveTo(r4222),
actionExecutor -> controller : ACTION_SUCCEEDED
```

However, as the semantic difference operator is by no means commutative, swapping the arguments changes the result.
Execute the following command:
```
java -jar SD4DevelopmentCLI.jar -sd -i rob1.sd rob2.sd
```

This yield the following output:
```
The input SD 'rob1.sd' is a refinement of the input SD 'rob2.sd'
```

In this case, the CLI tool outputs that the SD `rob1.sd` is a refinement of the SD 
`rob2.sd`. This means that every system run that is valid in the SD `rob1` is 
also valid in the SD `rob2`. Thus, the semantic difference from `rob1` to `rob2` is empty.

You finished the tutorial on semantic SD differencing and are now ready to execute 
semantic evolution analysis via semantic differencing for arbitrary SDs. Great!

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

