import { useEffect, useState } from 'react';
import type { TrainingSessionResponseDTO } from '../../services/training/training-session/training-session.dto';
import Loading from '../../components/common/Loading';
import ErrorMessage from '../../components/common/Error';
import type { PageResponseDTO } from '../../services/api.dto';
import { trainingSessionService } from '../../services/training/training-session/training-session.service';
import { Pagination } from '../../components/common/Pagination';
import TrainingSessionCard from '../../components/training/TrainingSessionCard';

export default function TrainingSessions() {
	const [trainingSessions, setTrainingSessions] = useState<TrainingSessionResponseDTO[]>([]);
	const [loading, setLoading] = useState<boolean>(false);
	const [error, setError] = useState<string | null>(null);

	const [page, setPage] = useState<number>(1);
	const [totalPages, setTotalPages] = useState<number>(1);
	const [totalElements, setTotalElements] = useState<number>(1);
	const [elementsPerPage, setElementsPerPage] = useState<number>(18);

	const fetchTrainingSessions = async () => {
		try {
			setError(null);
			setLoading(true);
			const data: PageResponseDTO<TrainingSessionResponseDTO> = await trainingSessionService.getTrainingSessions(page - 1, elementsPerPage);

			setTrainingSessions(data.content);
			setTotalPages(data.totalPages);
			setTotalElements(Number(data.totalElements));
		} catch (err) {
			console.error(err);
			setError("Failed to fetch exercises");
		} finally {
			setLoading(false);
		}
	};

	useEffect(() => {
		fetchTrainingSessions();
	}, [page, elementsPerPage]);

	if (loading) return <Loading />;
	if (error) {
		return (
			<div className="flex justify-center items-center h-full">
				<ErrorMessage
					title="Failed to load exercises"
					message="There was a problem connecting to the server. Please try again."
					onRetry={() => fetchTrainingSessions()}
				/>
			</div>
		)
	};

	return (
		<>
			<div className="w-full p-6 text-textcolor flex flex-col gap-4">

				<h1 className='text-red-500 font-bold'>O PLANO DE TREINO VAI SERVIR APENAS DE ESQUELETO PARA MONTAR A SESSÃO, APÓS SELECIONAR O PLANO DE TREINO É ADICIONADO À LISTA DE EXERCICIOS, E PODE SER ADICIONADO MAIS EXERCICIOS POSTERIORMENTE. ASSIM MESMO QUE UM PLANO DE TREINO SEJA APAGADO A SESSÃO CONTINUA
					<br />
					ALGUNS DTOS DO FRONTEND PODEM ESTAR DIFERENTES CUIDADO COM ISSO
				</h1>

				<div className="flex items-center justify-between gap-4">
					<button
						className="bg-primary p-2 px-4 rounded-xl cursor-pointer hover:bg-primary/80 font-semibold"
					>
						Create +
					</button>

					<div className="flex items-center gap-3">
						<div className="flex gap-3 items-center">
							<label htmlFor="exercisesPerPage" className="text-sm mb-1 font-extralight">
								Training sessions per page
							</label>

							<select
								id="exercisesPerPage"
								name="exercisesPerPage"
								required
								className="form-input w-14"
								value={elementsPerPage}
								onChange={(e) => setElementsPerPage(Number(e.target.value))}
							>
								<option value="12">12</option>
								<option value="18">18</option>
								<option value="24">24</option>
							</select>
						</div>

						<span className="text-secondary"> {totalElements} {totalElements === 1 ? "training session" : "training sessions"} </span>
					</div>

				</div>

				<div className="flex flex-col gap-2">
					<div className="grid grid-cols-1 md:grid-cols-2 2xl:grid-cols-3 gap-6">
						{trainingSessions.map((trainingSession) => (
							<TrainingSessionCard
								key={trainingSession.id}
								{...trainingSession}
							/>
						))}
					</div>
				</div>

				<div className="mb-4">
					<Pagination
						currentPage={page}
						totalPages={totalPages}
						onPageChange={(p: number) => setPage(p)}
					/>
				</div>
			</div>
		</>
	);
}