package main

import (
	"bytes"
	"fmt"
	"os"
	"os/exec"
    "strconv"
)

func main() {

    if len(os.Args) > 1 {
        cmd := os.Args[1]
        if cmd == "clear" {
            args := []string{"shell", "am", "broadcast", "-a", "kidding.command", "--es", "ipv4", "\"\""}
            stdOut, stdErr, _ := exeCommand("adb", args...)

            fmt.Print(string(stdOut))
            fmt.Print(string(stdErr))
        } else if cmd == "choose" {
            if len(os.Args) > 2 {
                index, _ := strconv.Atoi(os.Args[2])
                chooseIfConfig(index)
            }
        }

        return
    }

    chooseIfConfig(1)
}

func chooseIfConfig(index int) {
    stdOut, _, err := exeCommand("ifconfig")
    if err != nil {
        os.Exit(1)
        return
    }

    out := string(stdOut)
    ips := ParseIfConfig(out)
    ips = FilterPrivateAddr(ips)

    for i, ip := range ips {
        fmt.Printf("%d: %s\n", i+1, ip.Ipv4)
    }

    if len(ips) == 0 {
        println("no available network")
        return
    }

    if index > 0 && index <= len(ips) { // 这里用==1
        ip := ips[index - 1]
        fmt.Printf("choose %s automaticly", ip.Ipv4)
        fmt.Println()

        args := []string{"shell", "am", "broadcast", "-a", "kidding.command", "--es", "ipv4", "\"" + ip.Ipv4 + "\""}
        stdOut, stdErr, _ := exeCommand("adb", args...)

        fmt.Print(string(stdOut))
        fmt.Print(string(stdErr))
    } else {
        fmt.Println("no available network")
    }
}

// 执行外部命令
func exeCommand(name string, arg ...string) (stdOut []byte, stdErr []byte, err error) {
	//args := append([]string{}, arg...)
	//cmdLine := name + " " + strings.Join(args, " ")
	//println(cmdLine)

	var stdOutWriter bytes.Buffer
	var stdErrWriter bytes.Buffer

	cmd := exec.Command(name, arg...)
	cmd.Stdout = &stdOutWriter
	cmd.Stderr = &stdErrWriter

	err = cmd.Run()
	stdOut = stdOutWriter.Bytes()
	stdErr = stdErrWriter.Bytes()
	return
}
