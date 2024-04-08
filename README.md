# Wizard

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=SE2Project-BHKPTZ_frontend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=SE2Project-BHKPTZ_frontend)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=SE2Project-BHKPTZ_frontend&metric=coverage)](https://sonarcloud.io/summary/new_code?id=SE2Project-BHKPTZ_frontend)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=SE2Project-BHKPTZ_frontend&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=SE2Project-BHKPTZ_frontend)

This project is an implementation of the popular Wizard card game, a strategic game of prediction and trick-taking. Players must accurately predict the number of tricks they will win in each round. Correct guesses are rewarded, while incorrect predictions result in penalties.

## Configure environment variables

To run the projects you need to set the following environment variables in local.properties

- `api.url` - The url of the webserver
Example:

```
api.url=http://10.0.2.2:8081 # url used by the webserver (10.0.2.2 => localhost of your developing device)
```

## Contribution

This project follows the [conventional commit guidelines](https://www.conventionalcommits.org/en/v1.0.0/) and also enforces semantic PR titles to maintain a clear and understandable project history.

### Commit/PR Types and Scopes

For an up-to-date list of commit and PR types and their scopes, please refer to [this](https://github.com/SE2Project-BHKPTZ/frontend/blob/main/.github/workflows/validate-semantic-pr.yml).

#### Commit/PR Types Include:

- `feat`: Introduction of a new feature.
- `fix`: A bug correction.
- `build`: Modifications that affect the build system or external dependencies.
- `chore`: Miscellaneous changes that don't modify source or test files.
- `ci`: Adjustments to our CI configuration files and scripts.
- `docs`: Documentation updates.
- `perf`: Changes that enhance performance.
- `refactor`: Code alterations that neither fix a bug nor introduce a feature.
- `revert`: Undoing a previous commit.
- `style`: Changes that do not impact the code's meaning (white-space, formatting, etc.).
- `test`: Adding missing tests or correcting existing ones.

Example Commit and PR Title:

```
feat: Add login and register activities
```