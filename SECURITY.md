# Security Policy

## Supported Versions

| Version | Supported |
|---------|-----------|
| 1.x     | ✅ Active  |

## Reporting a Vulnerability

**Do not open a public issue for security vulnerabilities.**

Report privately via GitHub: [Security Advisories → Report a vulnerability](https://github.com/kimbongjune/geoserver-client/security/advisories/new)

Include:
- Description of the vulnerability
- Steps to reproduce
- Affected versions
- Suggested fix (optional)

You will receive a response within **7 days**. If the issue is confirmed, a patch will be released as soon as possible and you will be credited in the release notes (unless you prefer to remain anonymous).

## Automated Security Scanning

This repository runs the following checks on every push and weekly:

- **GitHub CodeQL** — static analysis for Java
- **OWASP Dependency-Check** — CVE scanning of all Maven dependencies (CVSS ≥ 9 fails the build)
- **Dependabot** — automated dependency update PRs (Maven + GitHub Actions, weekly)

Results are visible in the [Security tab](https://github.com/kimbongjune/geoserver-client/security).

## Scope

This library is a **REST API client** — it does not run a server, store credentials, or process untrusted input beyond what the caller passes in. The primary security concern is keeping transitive dependencies free of known CVEs.
