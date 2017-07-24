package main

import (
	"strconv"
	"strings"
)

// NetworkInfo struct
type NetworkInfo struct {
	Name string
	Ipv4 string
	Ipv6 string
}

// ParseIfConfig ...
func ParseIfConfig(out string) []*NetworkInfo {

	ips := make([]*NetworkInfo, 0)
	ls := make([]string, 0)

	split := strings.Split(out, "\n")
	for _, s := range split {
		if !strings.HasPrefix(s, "\t") {
			ip := parseLines(ls)
			if ip != nil {
				ips = append(ips, ip)
			}

			ls = make([]string, 0) // 清空slice
		}
		ls = append(ls, s)
	}

	return ips
}

// FilterPrivateAddr ...
func FilterPrivateAddr(ips []*NetworkInfo) (newIps []*NetworkInfo) {
	for _, ip := range ips {
		if len(ip.Ipv4) == 0 {
			continue
		}

		ss := strings.Split(ip.Ipv4, ".")
		if len(ss) != 4 {
			continue
		}

		// private address in A class
		// 10.0.0.0 - 10.255.255.255
		i1, _ := strconv.Atoi(ss[0])
		if i1 == 10 {
			newIps = append(newIps, ip)
			continue
		}

		// private address in B class
		// 172.16.0.0 - 172.31.255.255
		i2, _ := strconv.Atoi(ss[1])
		if i1 == 172 && i2 >= 16 && i2 <= 31 {
			newIps = append(newIps, ip)
			continue
		}

		// private address in C class
		// 192.168.0.0 - 192.168.255.255
		if i1 == 192 && i2 == 168 {
			newIps = append(newIps, ip)
			continue
		}
	}
	return
}

func isPrivateAddr(ip *NetworkInfo) bool {
    if len(ip.Ipv4) == 0 {
        return false
    }

    ss := strings.Split(ip.Ipv4, ".")
    if len(ss) != 4 {
        return false
    }

    // private address in A class
    // 10.0.0.0 - 10.255.255.255
    i1, _ := strconv.Atoi(ss[0])
    if i1 == 10 {
        return true
    }

    // private address in B class
    // 172.16.0.0 - 172.31.255.255
    i2, _ := strconv.Atoi(ss[1])
    if i1 == 172 && i2 >= 16 && i2 <= 31 {
        return true
    }

    // private address in C class
    // 192.168.0.0 - 192.168.255.255
    if i1 == 192 && i2 == 168 {
        return true
    }

    return false
}

func parseLines(ls []string) *NetworkInfo {
	if len(ls) == 0 {
		return nil
	}

	ss := strings.SplitN(ls[0], ":", 2)
	if len(ss) <= 1 {
		return nil
	}

	ip := &NetworkInfo{Name: strings.TrimSpace(ss[0])}

	for i := 1; i < len(ls); i++ {
		ss = strings.Split(strings.TrimSpace(ls[i]), " ")
		if len(ss) < 2 {
			continue
		}

		// find ip address
		if ss[0] == "inet" {
			ip.Ipv4 = ss[1]
		} else if ss[1] == "inet6" {
			ip.Ipv6 = ss[1]
		}
	}
	return ip
}
