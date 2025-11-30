"use client";
import React, {useEffect, useState} from 'react';
import { HiOutlineOfficeBuilding } from "react-icons/hi";
import { MdOutlinePeopleAlt } from "react-icons/md";

const OrganisationPage = () => {

  const [form, setForm] = useState({
    name: "",
    address: "",
    description: "",
    phoneNumber: "",
  });

  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(null);
  const [error, setError] = useState(null);
  const [isModalOpen, setIsModalOpen] = React.useState(false);
  const [modalVisible, setModalVisible] = React.useState(false);

  const [roles, setRoles] = useState([]);

  const openModal = () => {
    setIsModalOpen(true);
    setTimeout(() => {
      setModalVisible(true);
    }, 10);
  }
  const closeModal = () => {
    setModalVisible(false);
    setTimeout(() => {
      setIsModalOpen(false);
    }, 300);
  }

    const loadRoles = React.useCallback(async () => {
      try {
      const res = await fetch(
          `${process.env.NEXT_PUBLIC_API_BASE_URL}/organizations/roles`,
          { credentials: "include" }
      );
      const data = await res.json();
      console.log("roles : " ,data)
      setRoles(data);
      } catch (e) {
        setError(e.message);
      } finally {
        setLoading(false);
      }
  }, [] );

  React.useEffect(() => {
    loadRoles();
  }, [loadRoles]);
   function handleChange(e) {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    const isEmpty =
        !form.name.trim() &&
        !form.address.trim() &&
        !form.description.trim() &&
        !form.phoneNumber.trim();

    if (isEmpty) {
      setError("Veuillez remplir au moins un champ avant de sauvegarder.");
      return;
    }

    setLoading(true);

    try {
      const res = await fetch(
          `${process.env.NEXT_PUBLIC_API_BASE_URL}/organizations/update-details`,
          {
            method: "PATCH",
            headers: {
              "Content-Type": "application/json",
            },
            credentials: "include",
            body: JSON.stringify(form),
          }
      );

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Erreur serveur");
      }

      setSuccess("Organisation mise à jour avec succès !");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }
  return (
    <div className=" flex flex-col min-h-screen gap-6  bg-slate-50">

      <div className="flex flex-col gap-2">
        <h1 className="text-2xl  text-gray-900">
          Configuration de l'organisation
        </h1>
        <p className="mt-1 text-sm text-gray-400">
          Gérez les paramètres et les informations de votre organisation ici.
        </p>
      </div>

      <div>
        <div className="rounded-2xl bg-white p-4 shadow-sm border ">
          <div className=" flex flex-col gap-2">
          <div className="flex items-center gap-2">
            <HiOutlineOfficeBuilding className="h-6 w-6 text-gray-900" />
            <h2 className="text-md text-gray-900"> Informations de l'organisation  </h2>
          </div>
            <p className=" text-gray-500">Configurez les informations générales de votre organisation </p>
          </div>

            <div className="flex flex-col ">
              <form onSubmit={handleSubmit} className="grid grid-cols-2 gap-4 mt-4">

                {/* Nom */}
                <div className="flex flex-col">
                  <label className="text-sm font-medium text-slate-600">Nom</label>
                  <input
                      type="text"
                      name="name"
                      value={form.name}
                      onChange={handleChange}
                      className="rounded-lg border border-slate-300 bg-white p-2 text-sm"
                  />
                </div>

                {/* Adresse */}
                <div className="flex flex-col">
                  <label className="text-sm font-medium text-slate-600">Adresse</label>
                  <input
                      type="text"
                      name="address"
                      value={form.address}
                      onChange={handleChange}
                      className="rounded-lg border border-slate-300 bg-white p-2 text-sm"
                  />
                </div>
                {/* Téléphone */}
                <div className="flex flex-col">
                  <label className="text-sm font-medium text-slate-600">Téléphone</label>
                  <input
                      type="text"
                      name="phoneNumber"
                      value={form.phoneNumber}
                      onChange={handleChange}
                      className="rounded-lg border border-slate-300 bg-white p-2 text-sm"
                      placeholder="+33 6 12 34 56 78"
                  />
                </div>
                {/* Description */}
                <div className="flex flex-col col-span-2">
                  <label className="text-sm font-medium text-slate-600">Description</label>
                  <textarea
                      name="description"
                      value={form.description}
                      onChange={handleChange}
                      className="rounded-lg border border-slate-300 bg-white p-2 text-sm"
                      rows={3}
                  />
                </div>



                {/* Messages */}
                {error && (
                    <p className="text-red-600 text-sm bg-red-100 p-2 rounded ">
                      {error}
                    </p>
                )}

                {success && (
                    <p className="text-green-600 text-sm bg-green-100 p-2 rounded">
                      {success}
                    </p>
                )}

                {/* Bouton */}
                <button
                    type="submit"
                    disabled={loading}
                    className="w-1/4 mt-2 col-span-2 rounded-lg bg-black text-white p-2 text-sm font-medium hover:bg-gray-200 hover:text-black disabled:bg-gray-300 duration-300 cursor-pointer"
                >
                  {loading ? "Enregistrement..." : "Sauvegarder"}
                </button>
              </form>
            </div>
        </div>
      </div>
      <div className="rounded-2xl bg-white p-4 shadow-sm border flex flex-col gap-4 ">
        <div className=" flex justify-between items-center gap-2">
          <div className="flex flex-col   gap-2">
            <div className="flex items-center  gap-2">
              <MdOutlinePeopleAlt className="h-6 w-6 text-gray-900" />
              <h2 className="text-md text-gray-900"> Rôles </h2>
            </div>

            <p className=" text-gray-500">Configurez les rôles disponibles pour vos utilisateurs (<span>{roles.length} rôles</span>) </p>

          </div>
          <button onClick={openModal} className="rounded-lg bg-black px-4 py-2 text-sm font-medium text-white hover:bg-gray-200 hover:text-black cursor-pointer duration-300">
            Ajouter un rôle
          </button>

        </div>
        <div>
          <table className="w-full text-sm text-left">
            <thead>
            <tr className="border-b bg-slate-50">
              <th className="px-3 py-2 font-medium text-gray-600">Nom du rôle</th>
              <th className="px-3 py-2 font-medium text-gray-600">Description</th>
              <th className="px-3 py-2 font-medium text-gray-600 text-right">
                Actions
              </th>
            </tr>
            </thead>

            <tbody>
            {roles.length > 0 ? (
                roles.map((role) => (
                    <tr key={role.id} className="border-b hover:bg-slate-50">
                      <td className="px-3 py-2 text-gray-900">{role.name}</td>
                      <td className="px-3 py-2 text-gray-600">
                        {role.description || (
                            <span className="italic text-gray-400">
                    Aucune description
                  </span>
                        )}
                      </td>
                      <td className="px-3 py-2 text-right">
                        <button className="text-xs text-blue-600 hover:underline mr-3">
                          Modifier
                        </button>
                        <button className="text-xs text-red-600 hover:underline">
                          Supprimer
                        </button>
                      </td>
                    </tr>
                ))
            ) : (
                <tr>
                  <td
                      colSpan={3}
                      className="px-3 py-4 text-center text-gray-400 italic"
                  >
                    Aucun rôle configuré pour cette organisation.
                  </td>
                </tr>
            )}
            </tbody>
          </table>
        </div>


      </div>

    </div>
  );
};

export default OrganisationPage;