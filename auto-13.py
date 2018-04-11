import sys
import json
import re
import datetime as dt
from urllib import request, response, error


jira_api_base = "http://jira.nhst.no/rest/api/2/"
default_headers = {
    "Accept" : "application/json",
    "Content-Type": "application/json; charset=UTF-8",
    "Authorization": "Basic ZGV3YW46ZGV3MUFMQTJiM20="
}


def make_request(url, method, data):
    req = request.Request(url=url, method=method, headers=default_headers)
    if data:
        req.data = data
    return request.urlopen(req)


def determine_next_version():
    today = dt.date.today()
    weekday = today.weekday()
    days_to_next_release = dt.timedelta(days=(8 - weekday))
    next_release_day = today + days_to_next_release
    return next_release_day.isoformat()


def update_issue_version(issue, version):
    url = "{0}issue/{1}".format(jira_api_base, issue)
    data = "{\"update\":{\"fixVersions\":[{\"add\":{\"name\":\""+version+"\"}}]}}"
    method = "PUT"
    make_request(url, method, data.encode("utf-8"))


def create_version(version, project):
    url = jira_api_base+"version"
    data = "{\"name\":\""+version+"\",\"archived\":false,\"released\":false,\"project\":\""+project+"\"}"
    method = "POST"
    make_request(url, method, data.encode("utf-8"))


def proceed(issue, project):
    version = determine_next_version()
    try:
        update_issue_version(issue, version)
    except error.HTTPError as err:
        if err.code == 400:
            create_version(version, project)
            update_issue_version(issue, version)

commit_msg = "AUTO-13this is a dummy commit message"
match = re.match("(([A-Z]+)-[0-9]+).*", commit_msg, re.M)
if match:
    proceed(match.group(1), match.group(2))
