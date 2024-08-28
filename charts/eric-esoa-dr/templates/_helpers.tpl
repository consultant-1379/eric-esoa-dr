{{/*
Expand the name of the chart.
*/}}
{{- define "eric-esoa-dr.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create release name used for cluster role.
*/}}
{{- define "eric-esoa-dr.release.name" -}}
{{- default .Release.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Merge eric-product-info, user-defined annotations, and Prometheus annotations into a single set
of metadata annotations.
*/}}
{{- define "eric-esoa-dr.annotations" -}}
  {{- $productInfo := include "eric-esoa-dr.product-info" . | fromYaml -}}
  {{- $config := include "eric-esoa-dr.config-annotations" . | fromYaml -}}
  {{- include "eric-esoa-dr.mergeAnnotations" (dict "location" .Template.Name "sources" (list $productInfo $config)) | trim }}
{{- end -}}

{{/*
The name of the cluster role used during openshift deployments.
This helper is provided to allow use of the new global.security.privilegedPolicyClusterRoleName if set, otherwise
use the previous naming convention of <release_name>-allowed-use-privileged-policy for backwards compatibility.
*/}}
{{- define "eric-esoa-dr.privileged.cluster.role.name" -}}
  {{- if hasKey (.Values.global.security) "privilegedPolicyClusterRoleName" -}}
    {{ .Values.global.security.privilegedPolicyClusterRoleName }}
  {{- else -}}
    {{ template "eric-esoa-dr.release.name" . }}-allowed-use-privileged-policy
  {{- end -}}
{{- end -}}

{{/*
Define the log streaming method (DR-470222-010)
*/}}
{{- define "eric-esoa-dr.streamingMethod" -}}
{{- $streamingMethod := "direct" -}}
{{- if .Values.global -}}
  {{- if .Values.global.log -}}
      {{- if .Values.global.log.streamingMethod -}}
        {{- $streamingMethod = .Values.global.log.streamingMethod }}
      {{- end -}}
  {{- end -}}
{{- end -}}
{{- if .Values.log -}}
  {{- if .Values.log.streamingMethod -}}
    {{- $streamingMethod = .Values.log.streamingMethod }}
  {{- end -}}
{{- end -}}
{{- print $streamingMethod -}}
{{- end -}}

{{/*
Create Ericsson product app.kubernetes.io info
*/}}
{{- define "eric-esoa-dr.kubernetes-io-info" -}}
app.kubernetes.io/name: {{ .Chart.Name | quote }}
app.kubernetes.io/version: {{ .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" | quote }}
app.kubernetes.io/instance: {{ .Release.Name | quote }}
{{- end -}}

{{/*
Create Ericsson Product Info
*/}}
{{- define "eric-esoa-dr.product-info" -}}
ericsson.com/product-name: {{ (fromYaml (.Files.Get "eric-product-info.yaml")).productName | quote }}
ericsson.com/product-number: {{ (fromYaml (.Files.Get "eric-product-info.yaml")).productNumber | quote }}
ericsson.com/product-revision: {{ regexReplaceAll "(.*)[+|-].*" .Chart.Version "${1}" | quote }}
{{- end}}

{{/*
Create user-defined annotations
*/}}
{{ define "eric-esoa-dr.config-annotations" }}
  {{- $global := (.Values.global).annotations -}}
  {{- $service := .Values.annotations -}}
  {{- include "eric-esoa-dr.mergeAnnotations" (dict "location" .Template.Name "sources" (list $global $service)) }}
{{- end }}

{{- /*
Wrapper functions to set the contexts
*/ -}}
{{- define "eric-esoa-dr.mergeAnnotations" -}}
  {{- include "eric-esoa-dr.aggregatedMerge" (dict "context" "annotations" "location" .location "sources" .sources) }}
{{- end -}}
{{- define "eric-esoa-dr.mergeLabels" -}}
  {{- include "eric-esoa-dr.aggregatedMerge" (dict "context" "labels" "location" .location "sources" .sources) }}
{{- end -}}

{{- /*
Generic function for merging annotations and labels (version: 1.0.1)
{
    context: string
    sources: [[sourceData: {key => value}]]
}
This generic merge function is added to improve user experience
and help ADP services comply with the following design rules:
  - DR-D1121-060 (global labels and annotations)
  - DR-D1121-065 (annotations can be attached by application
                  developers, or by deployment engineers)
  - DR-D1121-068 (labels can be attached by application
                  developers, or by deployment engineers)
  - DR-D1121-160 (strings used as parameter value shall always
                  be quoted)
Installation or template generation of the Helm chart fails when:
  - same key is listed multiple times with different values
  - when the input is not string
IMPORTANT: This function is distributed between services verbatim.
Fixes and updates to this function will require services to reapply
this function to their codebase. Until usage of library charts is
supported in ADP, we will keep the function hardcoded here.
*/ -}}
{{- define "eric-esoa-dr.aggregatedMerge" -}}
  {{- $merged := dict -}}
  {{- $context := .context -}}
  {{- $location := .location -}}
  {{- range $sourceData := .sources -}}
    {{- range $key, $value := $sourceData -}}
      {{- /* FAIL: when the input is not string. */ -}}
      {{- if not (kindIs "string" $value) -}}
        {{- $problem := printf "Failed to merge keys for \"%s\" in \"%s\": invalid type" $context $location -}}
        {{- $details := printf "in \"%s\": \"%s\"." $key $value -}}
        {{- $reason := printf "The merge function only accepts strings as input." -}}
        {{- $solution := "To proceed, please pass the value as a string and try again." -}}
        {{- printf "%s %s %s %s" $problem $details $reason $solution | fail -}}
      {{- end -}}
      {{- if hasKey $merged $key -}}
        {{- $mergedValue := index $merged $key -}}
        {{- /* FAIL: when there are different values for a key. */ -}}
        {{- if ne $mergedValue $value -}}
          {{- $problem := printf "Failed to merge keys for \"%s\" in \"%s\": key duplication in" $context $location -}}
          {{- $details := printf "\"%s\": (\"%s\", \"%s\")." $key $mergedValue $value -}}
          {{- $reason := printf "The same key cannot have different values." -}}
          {{- $solution := "To proceed, please resolve the conflict and try again." -}}
          {{- printf "%s %s %s %s" $problem $details $reason $solution | fail -}}
        {{- end -}}
      {{- end -}}
      {{- $_ := set $merged $key $value -}}
    {{- end -}}
  {{- end -}}
{{- /*
Strings used as parameter value shall always be quoted. (DR-D1121-160)
The below is a workaround to toYaml, which removes the quotes.
Instead we loop over and quote each value.
*/ -}}
{{- range $key, $value := $merged }}
{{ $key }}: {{ $value | quote }}
{{- end -}}
{{- end -}}


{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "eric-esoa-dr.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Merge kubernetes-io-info, user-defined labels, and app and chart labels into a single set
of metadata labels.
*/}}
{{- define "eric-esoa-dr.labels" -}}
  {{- $kubernetesIoInfo := include "eric-esoa-dr.kubernetes-io-info" . | fromYaml -}}
  {{- $config := include "eric-esoa-dr.config-labels" . | fromYaml -}}
  {{- $appAndChartLabels := include "eric-esoa-dr.app-and-chart-labels" . | fromYaml -}}
  {{- include "eric-esoa-dr.mergeLabels" (dict "location" .Template.Name "sources" (list $kubernetesIoInfo $config $appAndChartLabels)) | trim }}
{{- end -}}

{{/*
Create user-defined labels
*/}}
{{ define "eric-esoa-dr.config-labels" }}
  {{- $global := (.Values.global).labels -}}
  {{- $service := .Values.labels -}}
  {{- include "eric-esoa-dr.mergeLabels" (dict "location" .Template.Name "sources" (list $global $service)) }}
{{- end }}

{{/*
Create app and chart metadata labels
*/}}
{{- define "eric-esoa-dr.app-and-chart-labels" -}}
app: {{ template "eric-esoa-dr.name" . }}
chart: {{ template "eric-esoa-dr.chart" . }}
{{- end -}}

{{/*
Create the name of the service account to use
*/}}
{{- define "eric-esoa-dr.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "eric-esoa-dr.name" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create the FQDN url to be used by the integration chart
*/}}
{{- define "eric-esoa-dr.fqdn" -}}
{{- $fqdnPrefix := .Values.ingress.fqdnPrefix -}}
{{- $chartName := .Chart.Name -}}
{{- if index .Values.global.hosts $chartName  -}}
  {{- $fqdn := index .Values.global.hosts $chartName -}}
  {{- printf "%s" $fqdn }}
{{- else -}}
  {{- $baseHostname := .Values.global.ingress.baseHostname | required "global.ingress.baseHostname is mandatory" -}}
  {{- printf "%s.%s" $fqdnPrefix $baseHostname }}
{{- end -}}
{{- end -}}

{{/*
Create the ingressClass to be used by the integration chart
*/}}
{{- define "eric-esoa-dr.ingressClass" -}}
{{- if .Values.ingress.ingressClass -}}
  {{- printf "%s" .Values.ingress.ingressClass }}
{{- else -}}
  {{- $ingressClass := .Values.global.ingress.ingressClass | required "global.ingress.ingressClass is mandatory" -}}
  {{- printf "%s" $ingressClass }}
{{- end -}}
{{- end -}}

{{/*
Any image path (DR-D1121-067)
*/}}
{{- define "eric-esoa-dr.imagePath" }}
    {{- $imageId := index . "imageId" -}}
    {{- $values := index . "values" -}}
    {{- $files := index . "files" -}}
    {{- $productInfo := fromYaml ($files.Get "eric-product-info.yaml") -}}
    {{- $registryUrl := index $productInfo "images" $imageId "registry" -}}
    {{- $repoPath := index $productInfo "images" $imageId "repoPath" -}}
    {{- $name := index $productInfo "images" $imageId "name" -}}
    {{- $tag :=  index $productInfo "images" $imageId "tag" -}}
    {{- if $values.global -}}
        {{- if $values.global.registry -}}
            {{- $registryUrl = default $registryUrl $values.global.registry.url -}}
        {{- end -}}
        {{- if not (kindIs "invalid" $values.global.registry.repoPath) -}}
            {{- $repoPath = $values.global.registry.repoPath -}}
        {{- end -}}
    {{- end -}}
    {{- if $values.imageCredentials -}}
        {{- if $values.imageCredentials.registry -}}
            {{- $registryUrl = default $registryUrl $values.imageCredentials.registry.url -}}
        {{- end -}}
        {{- if not (kindIs "invalid" $values.imageCredentials.repoPath) -}}
            {{- $repoPath = $values.imageCredentials.repoPath -}}
        {{- end -}}
        {{- $image := index $values.imageCredentials $imageId -}}
        {{- if $image -}}
            {{- if $image.registry -}}
                {{- $registryUrl = default $registryUrl $image.registry.url -}}
            {{- end -}}
            {{- if not (kindIs "invalid" $image.repoPath) -}}
                {{- $repoPath = $image.repoPath -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}
    {{- if $repoPath -}}
        {{- $repoPath = printf "%s/" $repoPath -}}
    {{- end -}}
    {{- printf "%s/%s%s:%s" $registryUrl $repoPath $name $tag -}}
{{- end -}}
{{/*
Enable Node Selector functionality
*/}}
{{- define "eric-esoa-dr.nodeSelector" -}}
{{- if .Values.global.nodeSelector }}
nodeSelector:
  {{ toYaml .Values.global.nodeSelector | trim }}
{{- else if .Values.nodeSelector }}
nodeSelector:
  {{ toYaml .Values.nodeSelector | trim }}
{{- end }}
{{- end -}}