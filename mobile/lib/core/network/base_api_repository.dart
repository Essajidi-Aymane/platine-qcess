import 'package:dio/dio.dart';

class ApiException implements Exception {
  final int? statusCode;
  final dynamic data;
  final String message;

  ApiException({this.statusCode, this.data, required this.message});

  @override
  String toString() => message;
}

abstract class BaseApiRepository {
  final Dio dio;

  BaseApiRepository(this.dio);

  Future<T> get<T>(
    String path, {
    Map<String, dynamic>? queryParameters,
    T Function(dynamic)? fromJson,
  }) async {
    try {
      final response = await dio.get(
        path,
        queryParameters: queryParameters,
      );
      if (fromJson != null) {
        return fromJson(response.data);
      }
      return response.data as T;
    } on DioException catch (e) {
      throw _toApiException(e);
    }
  }

  Future<T> post<T>(
    String path, {
    dynamic data,
    Map<String, dynamic>? queryParameters,
    T Function(dynamic)? fromJson,
  }) async {
    try {
      final response = await dio.post(
        path,
        data: data,
        queryParameters: queryParameters,
      );
      if (fromJson != null) {
        return fromJson(response.data);
      }
      return response.data as T;
    } on DioException catch (e) {
      throw _toApiException(e);
    }
  }

  Future<T> put<T>(
    String path, {
    dynamic data,
    Map<String, dynamic>? queryParameters,
    T Function(dynamic)? fromJson,
  }) async {
    try {
      final response = await dio.put(
        path,
        data: data,
        queryParameters: queryParameters,
      );
      if (fromJson != null) {
        return fromJson(response.data);
      }
      return response.data as T;
    } on DioException catch (e) {
      throw _toApiException(e);
    }
  }

  Future<T> delete<T>(
    String path, {
    dynamic data,
    Map<String, dynamic>? queryParameters,
    T Function(dynamic)? fromJson,
  }) async {
    try {
      final response = await dio.delete(
        path,
        data: data,
        queryParameters: queryParameters,
      );
      if (fromJson != null) {
        return fromJson(response.data);
      }
      return response.data as T;
    } on DioException catch (e) {
      throw _toApiException(e);
    }
  }

  ApiException _toApiException(DioException error) {
    final status = error.response?.statusCode;
    final data = error.response?.data;
    final message = switch (error.type) {
      DioExceptionType.badResponse => _extractServerMessage(data) ?? 'Erreur serveur',
      DioExceptionType.connectionTimeout => 'Délai d\'attente dépassé',
      DioExceptionType.receiveTimeout => 'Délai d\'attente dépassé',
      DioExceptionType.sendTimeout => 'Délai d\'attente dépassé',
      DioExceptionType.connectionError => 'Erreur réseau',
      DioExceptionType.cancel => 'Requête annulée',
      DioExceptionType.badCertificate => 'Erreur SSL',
      DioExceptionType.unknown => error.message ?? 'Erreur inconnue',
    };
    return ApiException(statusCode: status, data: data, message: message);
  }

  String? _extractServerMessage(dynamic responseData) {
    if (responseData is Map<String, dynamic>) {
      return responseData['message'] as String? ?? responseData['error'] as String?;
    }
    return null;
  }
}
